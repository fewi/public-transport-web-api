package de.fewi.ptwa.controller;

import de.fewi.ptwa.entity.DepartureData;
import de.fewi.ptwa.util.ProviderUtil;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.VagfrProvider;
import de.schildbach.pte.dto.Departure;
import de.schildbach.pte.dto.QueryDeparturesResult;
import de.schildbach.pte.dto.StationDepartures;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
public class DepartureController {
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");


    @RequestMapping(value = "/departure", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity departure(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "provider", required = false) String providerName, @RequestParam(value = "maxMinutes", defaultValue = "59") int maxValues) throws IOException {
        NetworkProvider provider = getNetworkProvider(providerName);
        if (provider == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider " + providerName + " not found or can not instantiated...");
        QueryDeparturesResult efaData = provider.queryDepartures(from, new Date(), maxValues, true);
        if (efaData.status.name().equals("OK")) {
            List<DepartureData> list = new ArrayList<>();
            if (efaData.findStationDepartures(from) == null && !efaData.stationDepartures.isEmpty()) {
                for (StationDepartures stationDeparture : efaData.stationDepartures) {
                    list.addAll(convertDepartures(stationDeparture));
                }
                Collections.sort(list);
            } else
                list.addAll(convertDepartures(efaData.findStationDepartures(from)));
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(list);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EFA error status: " + efaData.status.name());
    }

    @RequestMapping(value = "/departureFHEM", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity departureFHEM(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "provider", required = false) String providerName, @RequestParam(value = "maxMinutes", defaultValue = "59") int maxValues) throws IOException {
        NetworkProvider provider = getNetworkProvider(providerName);
        if (provider == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider " + providerName + " not found or can not instantiated...");
        QueryDeparturesResult efaData = provider.queryDepartures(from, new Date(), maxValues, true);
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
                for (DepartureData departureData : list) {
                    sb.append("[\"" + departureData.getNumber() + "\",\"" + departureData.getTo() + "\",\"" + departureData.getDepartureTimeInMinutes() + "\"],");
                }
                String lines = sb.toString();
                data = lines.substring(0, lines.lastIndexOf(',')) + "]";
            } else
                data = convertDeparturesFHEM(efaData.findStationDepartures(from));
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(data);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EFA error status: " + efaData.status.name());
    }

    private NetworkProvider getNetworkProvider(String providerName) {
        NetworkProvider provider;
        if (providerName != null) {
            provider = ProviderUtil.getObjectForProvider(providerName);
        } else
            provider = new VagfrProvider();
        return provider;
    }

    private String convertDeparturesFHEM(StationDepartures stationDepartures) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        Calendar cal = Calendar.getInstance();
        for (Departure departure : stationDepartures.departures) {
            long time = 0;
            if (departure.predictedTime != null && departure.predictedTime.after(departure.plannedTime)) {
                time = departure.predictedTime.getTime();
            } else {
                time = departure.plannedTime.getTime();
            }
            time = (time - cal.getTimeInMillis()) / 1000 / 60;
            sb.append("[\"" + departure.line.label + "\",\"" + departure.destination.name + "\",\"" + time + "\"],");
        }
        String lines = sb.toString();
        return lines.substring(0, lines.lastIndexOf(',')) + "]";
    }


    private List<DepartureData> convertDepartures(StationDepartures stationDepartures) {
        Calendar cal = Calendar.getInstance();
        List<DepartureData> list = new ArrayList();
        for (Departure departure : stationDepartures.departures) {
            DepartureData data = new DepartureData();
            data.setTo(departure.destination.name);
            data.setToId(departure.destination.id);
            data.setProduct(departure.line.product.toString());
            data.setNumber(departure.line.label);
            if (departure.position != null)
                data.setPlatform(departure.position.name);
            long time = 0;
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
            time = (time - cal.getTimeInMillis()) / 1000 / 60;
            data.setDepartureTimeInMinutes((int) time);
            list.add(data);
        }
        return list;
    }

}
