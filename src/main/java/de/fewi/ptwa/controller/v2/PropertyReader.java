/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fewi.ptwa.controller.v2;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author constantin
 */
public class PropertyReader {

    public static final PropertyReader INSTANCE = new PropertyReader();
    
    private PropertyReader() {
    }


    private final Map<String, Properties> propertyMap = new HashMap<>();
    
    public String getProperty(String propertyFile, String propertyKey, String defaultValue) {
        if (!propertyMap.containsKey(propertyFile)) {
            loadPropertiesFile(propertyFile);
        }
        return propertyMap.get(propertyFile).getProperty(propertyKey, defaultValue);
    }

    private void loadPropertiesFile(String propertyFile) {
        InputStream is = null;
        try {
            is = PropertyReader.class.getClassLoader().getResourceAsStream(propertyFile);
            propertyMap.put(propertyFile, new Properties());
            propertyMap.get(propertyFile).load(is);
        } catch (IOException ex) {
            throw new RuntimeException("Fehler beim Laden des Property Files '" + propertyFile +"'",ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }
        
    }
    
 
    
    
}
