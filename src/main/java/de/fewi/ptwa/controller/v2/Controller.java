/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fewi.ptwa.controller.v2;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    
    
}
