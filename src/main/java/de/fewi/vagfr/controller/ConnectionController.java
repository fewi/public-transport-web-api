package de.fewi.vagfr.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.schildbach.pte.VagfrProvider;
import de.schildbach.pte.dto.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ConnectionController {
    private final VagfrProvider provider = new VagfrProvider();

    @RequestMapping(value = "/connection", method = RequestMethod.GET)
    @ResponseBody
    public List<TripData> departure(@RequestParam(value = "from", required = true) String from, @RequestParam(value = "to", required = true) String to) throws IOException
    {
        char[] products ={ 'T'};
        QueryTripsResult efaData = provider.queryTrips(new Location(LocationType.STATION, from), null, new Location(LocationType.STATION, to), new Date(), true, Product.fromCodes(products), null, null, null, null);
        List<TripData> list = new ArrayList();
        for(Trip trip : efaData.trips){
            TripData data = new TripData();
            data.from = trip.from.name;
            data.fromId = trip.from.id;
            data.to = trip.to.name;
            data.toId = trip.to.id;
            //data.product = trip.
            //data.label = trip.getFirstPublicLeg().line.label;
            data.departureTime = trip.legs.get(0).getDepartureTime();

            list.add(data);
        }
        return list;
    }

    class TripData implements Serializable {
        String from;
        String fromId;

        String to;
        String toId;

        String product;
        String label;

        @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
        Date departureTime;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getFromId() {
            return fromId;
        }

        public void setFromId(String fromId) {
            this.fromId = fromId;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getToId() {
            return toId;
        }

        public void setToId(String toId) {
            this.toId = toId;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Date getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(Date departureTime) {
            this.departureTime = departureTime;
        }
    }
}
