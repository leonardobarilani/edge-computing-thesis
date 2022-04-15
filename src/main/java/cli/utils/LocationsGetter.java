package cli.utils;

import cli.infrastucture.Area;
import cli.infrastucture.Infrastructure;

import java.util.*;

public class LocationsGetter {

    /*
    * inEvery: specify the level of the hierarchy that will be affected.
    * inAreas: selects some subtrees. All the nodes in the subtrees of the specified
    *           kind will be affected unless exceptIn says otherwise.
    * exceptIn: selects some subtrees. All those nodes won't be affected
    * 
    * Example:  --inEvery district --inAreas C --exceptIn F
    *   A       continent
    *  / \
    * B   C     city
    * |\  |\
    * D E F G   district
    *
    * Execution:
    * 1. inEvery "district": D E F G
    * 2. inAreas "C": F G
    * 3. exceptIn "F": G
    * 4. Result: G
    * */

    private static int targetLevel;
    private static Set<String> inAreasSet;
    private static Set<String> exceptInSet;

    /**
     * Returns the locations of the infrastructure included in the deployment input.
     * @param infrastructure the Infrastructure object.
     * @param inEvery string of the --inEvery parameter.
     * @param inAreas array of strings of the --inAreas parameter.
     * @param exceptIn array of strings of the --exceptIn parameter.
     * @return an array of Area objects. Every location object has the fields: location_id, openfaas_gateway, openfaas_password, redis_host, redis_port, redis_password.
     */
    public static Area[] getAllLocations(
            Infrastructure infrastructure,
            String inEvery,
            String[] inAreas,
            String[] exceptIn) {
        String[] areaTypesIdentifiers = infrastructure.areaTypesIdentifiers;

        // if inAreas is not specified, we target all locations
        if (inAreas.length == 0)
			// TODO this is wrong, we have to get the root of the tree (example: p1)
            inAreas = new String[]{infrastructure.areaTypesIdentifiers[0]};

        // if inEvery is not specified, we target the lowest level
        targetLevel = areaTypesIdentifiers.length - 1;

        // targetLevel = areaTypesIdentifiers.indexOf(inEvery)
        for(int i = 0;i < areaTypesIdentifiers.length;i++)
            if (areaTypesIdentifiers[i].equals(inEvery))
                targetLevel = i;

        inAreasSet = new HashSet<>(Arrays.asList(inAreas));
        exceptInSet = new HashSet<>(Arrays.asList(exceptIn));

        return getAllLocations(
                infrastructure.hierarchy[0],
                false,
                0).toArray(Area[]::new);
    }

    private static List<Area> getAllLocations(
            Area current,       // current node
            boolean validArea,  // true when we are in a valid subtree
                                // (specified with the inArea parameter)
            int currentLevel    // depth in the hierarchy
    )
    {
        // if we find a node of the expectIn set, we stop the recursive search
        if (exceptInSet.contains(current.areaName))
            return new LinkedList<>();

        // if we are in a valid subtree, we update the validArea boolean
        if (inAreasSet.contains(current.areaName))
            validArea = true;

        // if we are in a valid subtree and in the selected level,
        // we have to add the current node to the selected areas
        if (validArea && currentLevel == targetLevel)
            return new LinkedList<>(List.of(current));

        // recursively call on children
        List<Area> result = new LinkedList<>();
        for(Area a : current.areas)
            result.addAll(getAllLocations(a,
                    validArea,
                    currentLevel + 1
                    ));
        return result;
    }
}
