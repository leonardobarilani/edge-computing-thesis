package com.openfaas.function.common;

import com.google.gson.Gson;
import com.openfaas.function.common.infrastucture.Area;
import com.openfaas.function.common.infrastucture.Infrastructure;

import java.util.Base64;

public class EdgeInfrastructureUtils {

    // common
    private static Infrastructure infrastructure;

    // getParentHost
    private static String thisLocationName;
    private static String parentLocationHost;
    private static boolean stopSearch;

    private EdgeInfrastructureUtils () { }

    // TODO this should be done once in the deployer by setting a PARENT_HOST env variable
    public static String getParentHost()
    {
        if (infrastructure == null)
        {
            String json = new String(Base64.getDecoder().decode(System.getenv("EDGE_INFRASTRUCTURE")));
            infrastructure = new Gson().fromJson(json, Infrastructure.class);
        }
        if (parentLocationHost == null)
        {
            thisLocationName = System.getenv("LOCATION_ID");
            getParentRecursive(infrastructure.hierarchy[0]);
        }
        return parentLocationHost;
    }
    private static void getParentRecursive(Area area)
    {
        if (stopSearch)
            return;
        for(var a : area.areas)
            if (a.areaName.equals(thisLocationName))
            {
                parentLocationHost = area.mainLocation.openfaas_gateway;
                stopSearch = true;
                return;
            }
        for (var a : area.areas)
            getParentRecursive(a);
    }

    // TODO this method can be changed in a hashtable generated at deployment time
    /**
     *
     * @param name is the areaName from the hierarchy json
     * @return openfaas_gateway associated to the areaName. null if there is no area with the associated name
     */
    public static String getGateway (String name) {
        if (infrastructure == null)
        {
            String json = new String(Base64.getDecoder().decode(System.getenv("EDGE_INFRASTRUCTURE")));
            infrastructure = new Gson().fromJson(json, Infrastructure.class);
        }

        return getGatewayRecursive(infrastructure.hierarchy[0], name);
    }
    private static String getGatewayRecursive(Area area, String name) {
        if (area.areaName.equals(name))
            return area.mainLocation.openfaas_gateway;
        for (var a : area.areas)
        {
            String gateway = getGatewayRecursive(a, name);
            if (gateway != null)
                return gateway;
        }
        return null;
    }
}
