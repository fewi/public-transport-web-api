package de.fewi.vagfr.controller;

import de.schildbach.pte.VagfrProvider;
import de.schildbach.pte.dto.*;
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
import java.util.Date;
import java.util.List;

import de.fewi.vagfr.entity.TripData;

@Controller
public class ConnectionController {
    private final VagfrProvider provider = new VagfrProvider();
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
    private Date plannedDepartureTime = new Date();

    @RequestMapping(value = "/connection", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity connection(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "to", required = true) String to, @RequestParam(value = "product", required = true) char product, @RequestParam(value = "timeOffset", required = true, defaultValue = "0") int timeOffset) throws IOException {
        plannedDepartureTime.setTime(new Date().getTime() + timeOffset * 60 * 1000);
        char[] products = {product};
        QueryTripsResult efaData = provider.queryTrips(new Location(LocationType.STATION, from), null, new Location(LocationType.STATION, to), plannedDepartureTime, true, Product.fromCodes(products), null, null, null, null);

        if (efaData.status.name().equals("OK")) {
            List<TripData> list = filterTrips(efaData.trips, from, to, "normal");

            if (list.size() < 1) {
                List<TripData> retryList = findMoreTrips(efaData.context, from, to, "normal");
                if (retryList.size() < 1)
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No trip found.");
                else
                    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(retryList);
            } else
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(list);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EFA error status: " + efaData.status.name());
    }

    @RequestMapping(value = "/departure", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity departure(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "maxMinutes", defaultValue = "59") int maxValues) throws IOException {
        QueryDeparturesResult efaData = provider.queryDepartures(from, null, maxValues, true);
        if (efaData.status.name().equals("OK")) {
            List<TripData> list = convertDepartures(efaData.findStationDepartures(from));
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(list);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EFA error status: " + efaData.status.name());
    }



    @RequestMapping(value = "/connectionEsp", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity departureEsp(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "to", required = true) String to, @RequestParam(value = "product", required = true) char product, @RequestParam(value = "timeOffset", required = true, defaultValue = "0") int timeOffset) throws IOException {
        plannedDepartureTime.setTime(new Date().getTime() + timeOffset * 60 * 1000);
        char[] products = {product};
        QueryTripsResult efaData = provider.queryTrips(new Location(LocationType.STATION, from), null, new Location(LocationType.STATION, to), plannedDepartureTime, true, Product.fromCodes(products), null, null, null, null);
        if (efaData.status.name().equals("OK")) {
            List<TripData> list = filterTrips(efaData.trips, from, to, "esp");

            if (list.size() < 1) {
                List<TripData> retryList = findMoreTrips(efaData.context, from, to, "esp");
                if (retryList.size() < 1)
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No trip found.");
                else
                    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{\"connections\":[{\"from\":{\"departureTime\":\"" + retryList.get(0).getDepartureTime() + "\",\"plannedDepartureTimestamp\":" + retryList.get(0).getPlannedDepartureTimestamp() + ",\"delay\":"+ retryList.get(0).getDepartureDelay()/60 + ",\"to\": \""+ retryList.get(0).getTo() + "\" }}]}");
            } else
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{\"connections\":[{\"from\":{\"departureTime\":\"" + list.get(0).getDepartureTime() + "\",\"plannedDepartureTimestamp\":" + list.get(0).getPlannedDepartureTimestamp() + ",\"delay\":"+ list.get(0).getDepartureDelay()/60 + ",\"to\": \""+ list.get(0).getTo() + "\" }}]}");

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EFA error status: " + efaData.status.name());
    }

    @RequestMapping(value = "/connectionRaw", method = RequestMethod.GET)
    @ResponseBody
    public List<Trip> test(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "to", required = true) String to, @RequestParam(value = "product", required = true) char product, @RequestParam(value = "timeOffset", required = true, defaultValue = "0") int timeOffset) throws IOException {
        plannedDepartureTime.setTime(new Date().getTime() + timeOffset * 60 * 1000);
        char[] products = {product};
        QueryTripsResult efaData = provider.queryTrips(new Location(LocationType.STATION, from), null, new Location(LocationType.STATION, to), plannedDepartureTime, true, Product.fromCodes(products), null, null, null, null);

        return efaData.trips;
    }

    private List<TripData> filterTrips(List<Trip> trips, String from, String to, String mode) {
        List<TripData> list = new ArrayList();
        for (Trip trip : trips) {
            Trip.Public leg = trip.getFirstPublicLeg();

            if (leg != null) {
                Date departureTime = leg.getDepartureTime();
                if (departureTime.after(plannedDepartureTime) && leg.departure.id.equals(from) && leg.arrival.id.equals(to) && !leg.departureStop.departureCancelled) {
                    TripData data = new TripData();
                    data.setFrom(trip.from.name);
                    data.setFromId(trip.from.id);
                    data.setTo(trip.to.name);
                    data.setToId(trip.to.id);
                    data.setProduct(leg.line.product.toString());
                    data.setNumber(leg.line.label);

                    //Planned time
                    data.setPlannedDepartureTime(df.format(leg.departureStop.plannedDepartureTime));
                    data.setPlannedDepartureTimestamp(leg.departureStop.plannedDepartureTime.getTime());

                    if (mode.equals("esp") && leg.departureStop.getDepartureDelay() / 1000 >= 60) {
                        //Correct time, because trams with delay arrive most time earlier
                        Date correctedTime = new Date(leg.departureStop.predictedDepartureTime.getTime() - 60000);
                        data.setDepartureTime(df.format((correctedTime)));
                        data.setDepartureTimestamp(correctedTime.getTime());

                    } else {
                        //Predicted time
                        data.setDepartureTime(df.format((leg.departureStop.predictedDepartureTime)));
                        data.setDepartureTimestamp(leg.departureStop.predictedDepartureTime.getTime());
                    }


                    data.setDepartureDelay(leg.departureStop.getDepartureDelay() / 1000);

                    list.add(data);
                }

            }

        }
        return list;
    }

    private List<TripData> convertDepartures(StationDepartures stationDepartures) {
       List<TripData> list = new ArrayList();
        for (Departure departure : stationDepartures.departures) {
               TripData data = new TripData();
                    data.setTo(departure.destination.name);
                    data.setToId(departure.destination.id);
                    data.setProduct(departure.line.product.toString());
                    data.setNumber(departure.line.label);
                    data.setPosition(departure.position.name);
                    //Planned time
                    data.setPlannedDepartureTime(df.format(departure.plannedTime));
                    data.setPlannedDepartureTimestamp(departure.plannedTime.getTime());
                    //Predicted time
                    if(departure.predictedTime != null) {
                        data.setDepartureTime(df.format((departure.predictedTime)));
                        data.setDepartureTimestamp(departure.predictedTime.getTime());
                    }
                    list.add(data);
        }
        return list;
    }


    private List<TripData> findMoreTrips(QueryTripsContext context, String from, String to, String mode) {
        List<TripData> data = new ArrayList();
        QueryTripsContext newContext = context;
        int count = 0;
        try {
            while (data.size() < 1) {
                if (count == 3)
                    break;
                else {
                    QueryTripsResult efaData = provider.queryMoreTrips(newContext, true);
                    newContext = efaData.context;
                    data = filterTrips(efaData.trips, from, to, mode);
                    count++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
