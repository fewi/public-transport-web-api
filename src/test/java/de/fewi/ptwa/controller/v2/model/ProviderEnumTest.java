/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fewi.ptwa.controller.v2.model;

import de.schildbach.pte.BvgProvider;
import de.schildbach.pte.KvvProvider;
import de.schildbach.pte.VbbProvider;
import de.schildbach.pte.VmsProvider;
import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author constantin
 */
public class ProviderEnumTest {
    
    public ProviderEnumTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    

    

    /**
     * Test for BVG.
     */
    @Test
    public void testBVG() {
        Assert.assertTrue(ProviderEnum.BVG.newNetworkProvider() instanceof BvgProvider);
        Assert.assertEquals("", "Berliner Verkehrsgesellschaft",ProviderEnum.BVG.label());
    }
    
    /**
     * Test for KVV.
     */
    @Test
    public void testKVV() {
        Assert.assertTrue(ProviderEnum.KVV.newNetworkProvider() instanceof KvvProvider);
        Assert.assertEquals("", "Karlsruher Verkehrsverbund",ProviderEnum.KVV.label());
    }
    
    /**
     * Test for VMS
     */
    @Test
    public void testVMS() {
        Assert.assertTrue(ProviderEnum.VMS.newNetworkProvider() instanceof VmsProvider );
        Assert.assertEquals("", "Verkehrsverbund Mittelsachsen",ProviderEnum.VMS.label());
    }
    
    /**
     * Test for VBB
     */
    @Test
    public void testVBB() {
        Assert.assertTrue(ProviderEnum.VBB.newNetworkProvider() instanceof VbbProvider);
        Assert.assertEquals("", "Verkehrsverbund Berlin Brandenburg",ProviderEnum.VBB.label());
    }
    
    
    
}
