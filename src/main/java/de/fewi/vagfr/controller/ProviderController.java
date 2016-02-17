package de.fewi.vagfr.controller;

import de.fewi.vagfr.entity.Provider;
import de.schildbach.pte.NetworkProvider;
import org.reflections.Reflections;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Controller
public class ProviderController {

    @RequestMapping(value = "/provider", method = RequestMethod.GET)
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

}
