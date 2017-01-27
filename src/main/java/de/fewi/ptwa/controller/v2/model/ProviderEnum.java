/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fewi.ptwa.controller.v2.model;

import de.fewi.ptwa.controller.v2.PropertyReader;
import de.schildbach.pte.BvgProvider;
import de.schildbach.pte.KvvProvider;
import de.schildbach.pte.NetworkProvider;
import de.schildbach.pte.VbbProvider;
import de.schildbach.pte.VmsProvider;

/**
 *
 * @author constantin
 */
public enum ProviderEnum {

    KVV(KvvProvider.class), BVG(BvgProvider.class), VMS(VmsProvider.class), VBB(VbbProvider.class);

   
    private final Class<? extends NetworkProvider> providerClass;

    ProviderEnum(Class<? extends NetworkProvider> providerClass) {
        this.providerClass = providerClass;
    }

    public String label() {
        return PropertyReader.INSTANCE.getProperty("de/fewi/ptwa/controller/v2/provider.properties", this.name().toLowerCase(), this.name());
    }

    public NetworkProvider newNetworkProvider() {
        try {
            return providerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("error on instantiation of networkprovider '" + name() + "'",ex);
        }
    }
    
    public Provider asProvider() {
        Provider provider = new Provider();
        provider.setDescription(this.label());
        provider.setName(this.name());                
        return provider;
    }

}
