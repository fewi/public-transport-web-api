package de.fewi.ptwa.controller;

import de.fewi.ptwa.util.ProviderUtil;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.VagfrProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


@Controller
public class StationController {


    @RequestMapping(value = "/station/suggest", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity suggest(@RequestParam("q") final String query, @RequestParam(value = "provider", required = false) String providerName) throws IOException {
        NetworkProvider provider;
        if (providerName != null) {
            provider = ProviderUtil.getObjectForProvider(providerName);
        } else
            provider = new VagfrProvider();
        if (provider == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider " + providerName + " not found or can not instantiated...");
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(provider.suggestLocations(query));
    }

}
