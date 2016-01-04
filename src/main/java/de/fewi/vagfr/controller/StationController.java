package de.fewi.vagfr.controller;

import de.schildbach.pte.VagfrProvider;
import de.schildbach.pte.dto.Departure;
import de.schildbach.pte.dto.QueryDeparturesResult;
import de.schildbach.pte.dto.StationDepartures;
import de.schildbach.pte.dto.SuggestLocationsResult;
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
import java.util.Date;


@Controller
public class StationController {

    private final VagfrProvider provider = new VagfrProvider();

    @RequestMapping(value = "/station/suggest", method = RequestMethod.GET)
    @ResponseBody
    public SuggestLocationsResult suggest(@RequestParam("q") final String query) throws IOException {
        return provider.suggestLocations(query);
    }

    @RequestMapping(value = "/station/nextDepartureEsp", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> departureEsp(@RequestParam("station") final String station) throws IOException {
        QueryDeparturesResult efaData = provider.queryDepartures(station, new Date(), 10, false);
        if (efaData.status.name().equals("OK")) {
            StationDepartures stationDepartures = efaData.stationDepartures.get(0);

            for (Departure departure : stationDepartures.departures) {
                if (departure.line.product.name().equals("TRAM") && (departure.destination.name.equals("Zähringen") || departure.destination.name.equals("Freiburg Hornusstraße"))) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

                    return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body("{\"connections\":[{\"from\":{\"departure\":\"" + df.format(departure.plannedTime) + "\"}}]}");
                }

            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @RequestMapping(value = "/station/nextDeparture", method = RequestMethod.GET)
    @ResponseBody
    public QueryDeparturesResult departure(@RequestParam("station") final String station, @RequestParam(value = "maxDepartures", required = false, defaultValue = "5") int maxDepartures) throws IOException {
        QueryDeparturesResult efaData = provider.queryDepartures(station, new Date(), maxDepartures, false);

        return efaData;
    }

}
