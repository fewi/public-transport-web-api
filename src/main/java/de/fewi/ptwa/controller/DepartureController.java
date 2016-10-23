package de.fewi.ptwa.controller;

import de.fewi.ptwa.entity.DepartureData;
import de.fewi.ptwa.util.ProviderUtil;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.VagfrProvider;
import de.schildbach.pte.dto.Departure;
import de.schildbach.pte.dto.QueryDeparturesResult;
import de.schildbach.pte.dto.StationDepartures;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class DepartureController {
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

    private static int counter = 0;

    @Value("${thingspeak.key}")
    private String thingspeakKey;

    @Value("${thingspeak.channel")
    private String thingspeakChannel;

    @RequestMapping(value = "/departure", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity departure(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "provider", required = false) String providerName, @RequestParam(value = "limit", defaultValue = "10") int limit) throws IOException {
        try {
            NetworkProvider provider = getNetworkProvider(providerName);
            if (provider == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider " + providerName + " not found or can not instantiated...");
            QueryDeparturesResult efaData = provider.queryDepartures(from, new Date(), 120, true);
            if (efaData.status.name().equals("OK")) {
                List<DepartureData> list = new ArrayList<>();
                if (efaData.findStationDepartures(from) == null && !efaData.stationDepartures.isEmpty()) {
                    for (StationDepartures stationDeparture : efaData.stationDepartures) {
                        list.addAll(convertDepartures(stationDeparture));
                    }
                    Collections.sort(list);
                } else
                    list.addAll(convertDepartures(efaData.findStationDepartures(from)));
                if(list.size() > limit)
                    list = list.subList(0,limit);
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(list);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EFA error status: " + efaData.status.name());
        }finally
        {
            counter++;
        }
    }

    @RequestMapping(value = "/departureFHEM", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity departureFHEM(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "provider", required = false) String providerName, @RequestParam(value = "limit", defaultValue = "10") int limit) throws IOException {
        try {
            NetworkProvider provider = getNetworkProvider(providerName);
            if (provider == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider " + providerName + " not found or can not instantiated...");
            QueryDeparturesResult efaData = provider.queryDepartures(from, new Date(), 120, true);
            if (efaData.status.name().equals("OK")) {
                String data = "";
                if (efaData.findStationDepartures(from) == null && !efaData.stationDepartures.isEmpty()) {
                    List<DepartureData> list = new ArrayList<>();
                    for (StationDepartures stationDeparture : efaData.stationDepartures) {
                        list.addAll(convertDepartures(stationDeparture));
                    }
                    Collections.sort(list);
                    StringBuffer sb = new StringBuffer();
                    sb.append("[");
                    int count = 0;
                    for (DepartureData departureData : list) {
                        sb.append("[\"" + departureData.getNumber() + "\",\"" + departureData.getTo() + "\",\"" + departureData.getDepartureTimeInMinutes() + "\"],");
                        count++;
                        if(count >= limit)
                            break;
                    }
                    String lines = sb.toString();
                    data = lines.substring(0, lines.lastIndexOf(',')) + "]";
                } else
                    data = convertDeparturesFHEM(efaData.findStationDepartures(from), limit);
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(data);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EFA error status: " + efaData.status.name());
        }finally {
            counter++;
        }

    }

    @Scheduled(initialDelay=10000, fixedRate=300000)
    public void doSomething() {
        if(thingspeakKey == null || thingspeakKey.isEmpty())
            return;
        String url = "http://api.thingspeak.com/update?key=";
        url += thingspeakKey;
        url += "&"+thingspeakChannel+"=";
        url += counter;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.getResponseCode();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        counter = 0;

    }

    private NetworkProvider getNetworkProvider(String providerName) {
        NetworkProvider provider;
        if (providerName != null) {
            provider = ProviderUtil.getObjectForProvider(providerName);
        } else
            provider = new VagfrProvider();
        return provider;
    }

    private String convertDeparturesFHEM(StationDepartures stationDepartures, int limit) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        Calendar cal = Calendar.getInstance();
        int count = 0;
        for (Departure departure : stationDepartures.departures) {
            long time = 0;
            if (departure.predictedTime != null && departure.predictedTime.after(departure.plannedTime)) {
                time = departure.predictedTime.getTime();
            } else {
                time = departure.plannedTime.getTime();
            }
            time = (time - cal.getTimeInMillis());
            float depMinutes = (float)time / 1000 / 60;
            sb.append("[\"" + departure.line.label + "\",\"" + departure.destination.name + "\",\"" + (int)Math.ceil(depMinutes) + "\"],");
            count++;
            if(count >= limit)
                break;
        }
        String lines = sb.toString();
        return lines.substring(0, lines.lastIndexOf(',')) + "]";
    }


    private List<DepartureData> convertDepartures(StationDepartures stationDepartures) {
        Calendar cal = Calendar.getInstance();
        List<DepartureData> list = new ArrayList();
        LocalDateTime endDate = LocalDateTime.now();
        for (Departure departure : stationDepartures.departures) {
            DepartureData data = new DepartureData();
            data.setTo(departure.destination.name);
            data.setToId(departure.destination.id);
            data.setProduct(departure.line.product.toString());
            data.setNumber(departure.line.label);
            if (departure.position != null)
                data.setPlatform(departure.position.name);
            long time;
            //Predicted time
            if (departure.predictedTime != null && departure.predictedTime.after(departure.plannedTime)) {
                data.setDepartureTime(df.format(departure.predictedTime));
                data.setDepartureTimestamp(departure.predictedTime.getTime());
                data.setDepartureDelay((departure.predictedTime.getTime() - departure.plannedTime.getTime()) / 1000 / 60);
                time = departure.predictedTime.getTime();
            } else {
                data.setDepartureTime(df.format(departure.plannedTime));
                data.setDepartureTimestamp(departure.plannedTime.getTime());
                time = departure.plannedTime.getTime();
            }
            time = (time - cal.getTimeInMillis());
            float depMinutes = (float)time / 1000 / 60;
            data.setDepartureTimeInMinutes((int) Math.ceil(depMinutes));
            list.add(data);
        }
        return list;
    }

}
