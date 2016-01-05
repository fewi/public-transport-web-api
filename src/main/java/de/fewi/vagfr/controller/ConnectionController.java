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
    public ResponseEntity<Object> departure(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "to", required = true) String to, @RequestParam(value = "product", required = true) char product, @RequestParam(value = "timeOffset", required = true, defaultValue = "0") int timeOffset) throws IOException
    {
        plannedDepartureTime.setTime(new Date().getTime() + timeOffset*60*1000);
        char[] products ={ product };
        QueryTripsResult efaData = provider.queryTrips(new Location(LocationType.STATION, from), null, new Location(LocationType.STATION, to), plannedDepartureTime, true, Product.fromCodes(products), null, null, null, null);

        if (efaData.status.name().equals("OK")) {
            List<TripData> list = filterTrips(efaData.trips, from, to);

            if(list.size() < 1){
                List<TripData> retryList = findMoreTrips(efaData.context, from, to);
                if(retryList.size() < 1)
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No trip found.");
                else
                    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(retryList);
            }

            else
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(list);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EFA error status: "+efaData.status.name());
    }

    @RequestMapping(value = "/connectionEsp", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> departureEsp(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "to", required = true) String to, @RequestParam(value = "product", required = true) char product, @RequestParam(value = "timeOffset", required = true, defaultValue = "0") int timeOffset) throws IOException
    {
        plannedDepartureTime.setTime(new Date().getTime() + timeOffset*60*1000);
        char[] products ={ product };
        QueryTripsResult efaData = provider.queryTrips(new Location(LocationType.STATION, from), null, new Location(LocationType.STATION, to), plannedDepartureTime, true, Product.fromCodes(products), null, null, null, null);
        if (efaData.status.name().equals("OK")) {
            List<TripData> list = filterTrips(efaData.trips, from, to);

            if(list.size() < 1){
                List<TripData> retryList = findMoreTrips(efaData.context, from, to);
                if(retryList.size() < 1)
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No trip found.");
                else
                    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{\"connections\":[{\"from\":{\"departure\":\"" + retryList.get(0).getDepartureTime() + "\"}}]}");
            }

            else
                return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{\"connections\":[{\"from\":{\"departure\":\"" + list.get(0).getDepartureTime() + "\"}}]}");

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EFA error status: "+efaData.status.name());
    }

    @RequestMapping(value = "/connectionRaw", method = RequestMethod.GET)
    @ResponseBody
    public List<Trip> test(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "to", required = true) String to, @RequestParam(value = "product", required = true) char product, @RequestParam(value = "timeOffset", required = true, defaultValue = "0") int timeOffset) throws IOException
    {
        plannedDepartureTime.setTime(new Date().getTime() + timeOffset*60*1000);
        char[] products ={ product };
        QueryTripsResult efaData = provider.queryTrips(new Location(LocationType.STATION, from), null, new Location(LocationType.STATION, to), plannedDepartureTime, true, Product.fromCodes(products), null, null, null, null);

        return efaData.trips;
    }

    private List<TripData> filterTrips(List<Trip> trips, String from, String to){
        List<TripData> list = new ArrayList();
        for(Trip trip : trips){
            Trip.Public leg = trip.getFirstPublicLeg();

            if(leg != null){
                Date departureTime =leg.getDepartureTime();
                if(departureTime.after(plannedDepartureTime) && leg.departure.id.equals(from) && leg.arrival.id.equals(to)){
                    TripData data = new TripData();
                    data.from = trip.from.name;
                    data.fromId = trip.from.id;
                    data.to = trip.to.name;
                    data.toId = trip.to.id;
                    data.product = leg.line.product.toString();
                    data.number = leg.line.label;
                    data.departureTime =  df.format(leg.getDepartureTime());
                    list.add(data);
                }

            }

        }
        return list;
    }


    private List<TripData> findMoreTrips(QueryTripsContext context, String from, String to){
        List<TripData> data = new ArrayList();
        QueryTripsContext newContext = context;
        int count = 0;
        try {
            while (data.size() < 1){
                if(count == 3)
                    break;
                else {
                    QueryTripsResult efaData = provider.queryMoreTrips(newContext, true);
                    newContext = efaData.context;
                    data = filterTrips(efaData.trips, from, to);
                    count++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
