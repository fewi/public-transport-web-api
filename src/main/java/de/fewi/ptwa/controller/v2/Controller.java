/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fewi.ptwa.controller.v2;

import de.fewi.ptwa.entity.Provider;
import de.fewi.ptwa.util.ProviderUtil;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.VagfrProvider;
import de.schildbach.pte.dto.Location;
import de.schildbach.pte.dto.LocationType;
import de.schildbach.pte.dto.SuggestLocationsResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.reflections.Reflections;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author constantin
 */
@org.springframework.stereotype.Controller
public class Controller {
    
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok("v2");
    }
    
    @RequestMapping(value = "/v2/provider", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity providerlist() throws IOException {
        List<Provider> list = new ArrayList();

        Set<Class<? extends NetworkProvider>> reflection = new Reflections("de.schildbach.pte").getSubTypesOf(NetworkProvider.class);
        for (Class<? extends NetworkProvider> implClass : reflection) {
            if(implClass.getSimpleName().startsWith("Abstract"))
                continue;
            Provider provider = new Provider();
            provider.setName(implClass.getSimpleName().substring(0, implClass.getSimpleName().indexOf("Provider")));
            provider.setClass(implClass.getSimpleName());
            list.add(provider);
        }
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(list);
    }
    
    @RequestMapping(value = "/v2/station/suggest", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity suggest(@RequestParam("q") final String query, @RequestParam(value = "provider", required = false) String providerName, @RequestParam(value = "locationType", required = false) String stationType) throws IOException {
        NetworkProvider provider;
        if (providerName != null) {
            provider = ProviderUtil.getObjectForProvider(providerName);
        } else
            provider = new VagfrProvider();
        if (provider == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provider " + providerName + " not found or can not instantiated...");
        SuggestLocationsResult suggestLocations = provider.suggestLocations(query);
        if (SuggestLocationsResult.Status.OK.equals(suggestLocations.status)) {
            Iterator<Location> iterator = suggestLocations.getLocations().iterator();
            LocationType locationType = getLocationType(stationType);
            List<Location> resultList = new ArrayList<>();
            if (locationType == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("LocationType " + stationType + " not found or can not instantiated...");
            } else if (!LocationType.ANY.equals(locationType)) {
                while (iterator.hasNext()) {
                    Location loc = iterator.next();
                    if (locationType.equals(loc.type)) {
                        resultList.add(loc);
                    }
                }
            } else {
                resultList.addAll(suggestLocations.getLocations());
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(resultList);    
        } else {
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Remote Service is down or temporarily not available");    
        }
    }
    
    
    private LocationType getLocationType(String locationType) {
        if (locationType == null || "*".equals(locationType)) {
            return LocationType.ANY;
        } else {
            try {
                return LocationType.valueOf(locationType.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
    
    
}
