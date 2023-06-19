package com.openfaas.function.utils;

import com.google.gson.Gson;
import com.openfaas.function.model.infrastucture.Area;
import com.openfaas.function.model.infrastucture.Infrastructure;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class EdgeInfrastructureUtils {

    // common
    public static Infrastructure infrastructure;

    // getParentHost
    private static String parentLocationHost;
    private static String parentLocationId;
    private static boolean stopSearch;

    private EdgeInfrastructureUtils() {
    }

    public static Infrastructure getInfrastructure () {
        if (infrastructure == null) {
            String json = new String(Base64.getDecoder().decode(System.getenv("EDGE_INFRASTRUCTURE")));
            infrastructure = new Gson().fromJson(json, Infrastructure.class);
        }
        return infrastructure;
    }

    // TODO this should be done once in the deployer by setting a PARENT_HOST env variable
    public static String getParentHost(String locationChild) {
        if (infrastructure == null) {
            String json = new String(Base64.getDecoder().decode(System.getenv("EDGE_INFRASTRUCTURE")));
            infrastructure = new Gson().fromJson(json, Infrastructure.class);
        }
        stopSearch = false;
        getParentRecursive(infrastructure.hierarchy[0], locationChild);
        return parentLocationHost;
    }

    public static String getParentLocationId(String locationChild) {
        if (infrastructure == null) {
            String json = new String(Base64.getDecoder().decode(System.getenv("EDGE_INFRASTRUCTURE")));
            infrastructure = new Gson().fromJson(json, Infrastructure.class);
        }
        stopSearch = false;
        getParentRecursive(infrastructure.hierarchy[0], locationChild);
        return parentLocationId;
    }

    private static void getParentRecursive(Area area, String locationChild) {
        if (stopSearch)
            return;
        for (var a : area.areas)
            if (a.areaName.equals(locationChild)) {
                parentLocationHost = area.mainLocation.openfaas_gateway;
                parentLocationId = area.areaName;
                stopSearch = true;
                return;
            }
        for (var a : area.areas)
            getParentRecursive(a, locationChild);
    }

    // TODO this method can be changed in a hashtable generated at deployment time

    /**
     * @param name is the areaName from the hierarchy json
     * @return openfaas_gateway associated to the areaName. null if there is no area with the associated name
     */
    public static String getGateway(String name) {
        if (infrastructure == null) {
            String json = new String(Base64.getDecoder().decode(System.getenv("EDGE_INFRASTRUCTURE")));
            infrastructure = new Gson().fromJson(json, Infrastructure.class);
        }

        return getGatewayRecursive(infrastructure.hierarchy[0], name);
    }

    private static String getGatewayRecursive(Area area, String name) {
        if (area.areaName.equals(name))
            return area.mainLocation.openfaas_gateway;
        for (var a : area.areas) {
            String gateway = getGatewayRecursive(a, name);
            if (gateway != null)
                return gateway;
        }
        return null;
    }

    /**
     * Return the list of all the locations contained in the subtree with the specified root
     *
     * @param root the name of the root node. It must be an areaName that is contained in the infrastructure.json
     * @return the list of the nodes in the subtree, including the root. Empty list if the specified root is not found in the infrastructure.json
     */
    public static List<String> getLocationsSubTree(String root) {
        if (infrastructure == null) {
            String json = new String(Base64.getDecoder().decode(System.getenv("EDGE_INFRASTRUCTURE")));
            infrastructure = new Gson().fromJson(json, Infrastructure.class);
        }

        List<String> locations = new LinkedList<>();
        getLocationsSubTreeRecursive(infrastructure.hierarchy[0], root, false, locations);
        return locations;
    }

    private static void getLocationsSubTreeRecursive(Area area, String root, boolean rootFound, List<String> locationsList) {
        if (area.areaName.equals(root))
            rootFound = true;

        if (rootFound)
            locationsList.add(area.areaName);

        for (var a : area.areas)
            getLocationsSubTreeRecursive(a, root, rootFound, locationsList);
    }


    // TODO testing this would be nice

    /**
     * Get all the locations from the level of the hierarchy to the specified nodeName.
     * The specified node must be under the level in the hierarchy.
     * The list is ordered from the specified level to the specified node.
     * The list also contains the specified node.
     *
     * @param nodeName
     * @param level
     * @return
     */
    public static List<String> getLocationsFromNodeToLevel(String nodeName, String level) {
        if (infrastructure == null) {
            String json = new String(Base64.getDecoder().decode(System.getenv("EDGE_INFRASTRUCTURE")));
            infrastructure = new Gson().fromJson(json, Infrastructure.class);
        }

        List<String> locations = new LinkedList<>();

        // nodeName not found
        if (!getLocationsFromRootToNodeRecursive(
                nodeName,
                infrastructure.hierarchy[0],
                locations
        ))
            return List.of();

        // find the depth of the level
        int levelDepth = -1;
        for (int i = 0; i < infrastructure.areaTypesIdentifiers.length; i++)
            if (infrastructure.areaTypesIdentifiers[i].equals(level))
                levelDepth = i;

        // level not found
        if (levelDepth == -1)
            return List.of();

        // level is deeper than in the hierarchy than the node
        if (levelDepth >= locations.size())
            return List.of();

        // trim the locations from the top level to the bottom location
        return locations.subList(levelDepth, locations.size());
    }

    // Get all the locations from the root of the hierarchy to the specified nodeName
    private static boolean getLocationsFromRootToNodeRecursive(String nodeName, Area area, List<String> locations) {
        locations.add(area.areaName);
        if (area.areaName.equals(nodeName))
            return true;

        for (Area a : area.areas)
            if (getLocationsFromRootToNodeRecursive(
                    nodeName,
                    a,
                    locations
            ))
                return true;

        if (!locations.isEmpty())
            locations.remove(locations.size() - 1);
        return false;
    }
}
