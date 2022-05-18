package com.openfaas.function.common.utils;

import com.google.gson.Gson;
import com.openfaas.function.common.infrastucture.Area;
import com.openfaas.function.common.infrastucture.Infrastructure;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

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

    /**
     * Return the list of all the locations contained in the subtree with the specified root
     * @param root the name of the root node. It must be an areaName that is contained in the infrastructure.json
     * @return the list of the nodes in the subtree, including the root. Empty list if the specified root is not found in the infrastructure.json
     */
    public static List<String> getLocationsSubTree (String root) {
        if (infrastructure == null)
        {
            String json = new String(Base64.getDecoder().decode(System.getenv("EDGE_INFRASTRUCTURE")));
            infrastructure = new Gson().fromJson(json, Infrastructure.class);
        }

        List<String> locations = new LinkedList<>();
        getLocationsSubTreeRecursive(infrastructure.hierarchy[0], root, false, locations);
        return locations;
    }
    private static void getLocationsSubTreeRecursive (Area area, String root, boolean rootFound, List<String> locationsList) {
        if (area.areaName.equals(root))
            rootFound = true;

        if (rootFound)
            locationsList.add(area.areaName);

        for (var a : area.areas)
            getLocationsSubTreeRecursive(a, root, rootFound, locationsList);
    }
}
