package de.fewi.vagfr.controller;

import de.fewi.vagfr.ProviderUtil;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.VagfrProvider;
import de.schildbach.pte.dto.SuggestLocationsResult;
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
    public SuggestLocationsResult suggest(@RequestParam("q") final String query, @RequestParam(value = "provider") String providerName) throws IOException {
        NetworkProvider provider;
        if (providerName != null) {
            provider = ProviderUtil.getObjectForProvider(providerName);
        } else
            provider = new VagfrProvider();
        return provider.suggestLocations(query);
    }

}
