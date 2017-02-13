package de.fewi.ptwa.util;

import de.schildbach.pte.AbstractNavitiaProvider;
import de.schildbach.pte.NetworkProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

@Component
public class ProviderUtil {

    private static String navitiaKey;
    
    private static String bvgKey;

    public static NetworkProvider getObjectForProvider(String providerName) {
        if(providerName == null || providerName.length() < 1)
            return null;
        try {
            Class<?> providerClass = Class.forName("de.schildbach.pte." + providerName + "Provider");
            if(providerClass.isAssignableFrom(AbstractNavitiaProvider.class))
            {
                return  (NetworkProvider)providerClass.getDeclaredConstructor(String.class).newInstance(navitiaKey);
            }
            if(providerName.equals("Bvg"))
            {
                return  (NetworkProvider)providerClass.getDeclaredConstructor(String.class).newInstance(bvgKey);
            }
            return (NetworkProvider)providerClass.newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }
    
    @Value("${providerkey.navitia}")
    public void setNavitiaKey(String navitiaKey) {
        ProviderUtil.navitiaKey = navitiaKey;
    }

    @Value("${providerkey.bvg}")
    public void setBvgKey(String bvgKey) {
        ProviderUtil.bvgKey = bvgKey;
    }
}
