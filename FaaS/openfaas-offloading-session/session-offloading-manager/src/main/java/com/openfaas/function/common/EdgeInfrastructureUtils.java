package com.openfaas.function.common;

import com.google.gson.Gson;
import com.openfaas.function.common.infrastucture.Area;
import com.openfaas.function.common.infrastucture.Infrastructure;

public class EdgeInfrastructureUtils {

    private static Infrastructure infrastructure;
    private static String thisLocationName;
    private static String parentLocationHost;
    private static boolean stopSearch;

    private EdgeInfrastructureUtils () { }

    public static String getParentHost()
    {
        // TODO this should be done once in the deployer by setting a PARENT_HOST env variable
        if (infrastructure == null)
        {
            infrastructure = new Gson().fromJson(System.getenv("EDGE_INFRASTRUCTURE"), Infrastructure.class);
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
}
