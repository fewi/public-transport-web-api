/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fewi.ptwa.controller.v2.model;

import de.fewi.ptwa.controller.v2.PropertyReader;
import de.schildbach.pte.AbstractNetworkProvider;
import de.schildbach.pte.BvgProvider;
import de.schildbach.pte.KvvProvider;
import de.schildbach.pte.VbbProvider;
import de.schildbach.pte.VmsProvider;

/**
 *
 * @author constantin
 */
public enum ProviderEnum {

    KVV(KvvProvider.class), BVG(BvgProvider.class), VMS(VmsProvider.class), VBB(VbbProvider.class);
    private final Class<? extends AbstractNetworkProvider> providerClass;

    ProviderEnum(Class<? extends AbstractNetworkProvider> providerClass) {
        this.providerClass = providerClass;
    }

    String label() {
        return PropertyReader.INSTANCE.getProperty("de/fewi/ptwa/controller/v2/provider.properties", this.name().toLowerCase(), this.name());
    }

    Class<? extends AbstractNetworkProvider> providerClass() {
        return providerClass;
    }

}
