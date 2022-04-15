package cli.utils;

import cli.infrastucture.Area;
import cli.infrastucture.Infrastructure;
import cli.infrastucture.OpenFaaSRedisConfiguration;

import java.util.*;

public class InfrastructureParser {

    public static boolean isInfrastructureJsonCorrect(Infrastructure infrastructure) {
        var hierarchy = infrastructure.hierarchy;
        var areaTypesIdentifiers = infrastructure.areaTypesIdentifiers;

        if(hierarchy == null || hierarchy.length == 0) {
            System.err.println("Error: field hierarchy not specified.");
            return false;
        } else if(areaTypesIdentifiers == null || areaTypesIdentifiers.length == 0) {
            System.err.println("Error: field areaTypesIdentifiers not specified.");
            return false;

        // !areaTypesIdentifiers.every((areaType) => canBeValidAreaType(areaType))
        } else if(!Arrays.stream(areaTypesIdentifiers)
                .map(InfrastructureParser::canBeValidAreaType)
                .reduce(true, (v1, v2) -> (v1 && v2))) {
            System.err.println("Error: area types must be string and different than reserved keywords.");
            return false;
        } else {
            return isHierarchyObjectCorrect(hierarchy[0], areaTypesIdentifiers);
        }
        //return true;
    }

/*
 * Return true if the deployment input (--inEvery, --inAreas, --exceptIn) are correct considering the infrastructure.
 * @param infrastructureJson the JSON of the infrastructure.
 * @param inEvery string of the --inEvery parameter.
 * @param inAreas array of strings of the --inAreas parameter.
 * @param exceptIn array of strings of the --exceptIn parameter.
 * @returns {boolean} true if the deployment input is correct, false otherwise.
 */
  /*  public static boolean isDeploymentInputCorrect(infrastructureJson, inEvery, inAreas, exceptIn) {
    const areaTypesIdentifiers = infrastructureJson.areaTypesIdentifiers;

        // Check that inEvery field is valid.
    const possibleAreaTypesIdentifiers = areaTypesIdentifiers.concat(["location"]);
    const inEveryLevel = possibleAreaTypesIdentifiers.indexOf(inEvery);
        if(inEveryLevel === -1) {
            console.log(chalk.red("Error: --inEvery is not a valid area type identifier. Valid identifiers for the infrastructure are: " + possibleAreaTypesIdentifiers + "."));
            return false;
        }

        // Check that inAreas field is valid.
        if(inAreas !== null && inAreas !== undefined) {
            if(!Array.isArray(inAreas)) {
                console.log(chalk.red("Error: field inAreas is not an array."));
                return false;
            }

            // The areas specified in inAreas must have an area type bigger or equal than the area type specified in inEvery.
            for(const areaName of inAreas) {
            const areaLevel = getAreaLevel(infrastructureJson, areaName);
                if(areaLevel === null) {
                    console.log(chalk.red("Error: --inAreas contains an area that does not exist in the infrastructure. Area: " + areaName + "."));
                    return false;
                }
                if(inEveryLevel < areaLevel) {
                    console.log(chalk.red("Error: the areas specified in --inAreas must have an area type bigger or equal than the area type specified in --inEvery."));
                    console.log(chalk.red("This error has been found while analyzing area: " + areaName + "."));
                    return false;
                }
            }
        }

        // Check that exceptIn field is valid.
        if(exceptIn !== null && exceptIn !== undefined) {
            if(!Array.isArray(exceptIn)) {
                console.log(chalk.red("Error: field exceptIn is not an array."));
                return false;
            }

            // The areas specified in exceptIn must have an area type bigger or equal than the area type specified in inEvery.
            for(const areaName of exceptIn) {
            const areaLevel = getAreaLevel(infrastructureJson, areaName);
                if(areaLevel === null) {
                    console.log(chalk.red("Error: --exceptIn contains an area that does not exist in the infrastructure. Area: " + areaName + "."));
                    return false;
                }
                if(inEveryLevel < areaLevel) {
                    console.log(chalk.red("Error: the areas specified in --exceptIn must have an area type bigger or equal than the area type specified in --inEvery."));
                    console.log(chalk.red("This error has been found while analyzing area: " + areaName + "."));
                    return false;
                }
            }
        }

        // All correct.
        return true;
    }*/




    private static final Set<String> reservedKeywords = new HashSet<>(Arrays.asList(
        "location_id", // where is even used location_id?
        "openfaas_gateway",
        "openfaas_password",
        "redis_host",
        "redis_port",
        "redis_password",
        "areaName",
        "mainLocation"
    ));

    // Is this necessary or is this just a JavaScript problem?
    private static boolean canBeValidAreaType(String areaType) {
        return !reservedKeywords.contains(areaType);
    }

    private static Set<String> areaNames; // Used to check if area name is unique.
    private static boolean isHierarchyObjectCorrect(Area hierarchy, String[] areaTypesIdentifiers) {
        areaNames = new HashSet<>();
        return isAreasContainerCorrect(hierarchy, areaTypesIdentifiers, 0);
    }

    private static boolean isAreasContainerCorrect(Area areasContainer, String[] areaTypesIdentifiers, int level) {
        // Check if it is an areas container or a locations container (last level).
        if (areasContainer.areas.length != 0){

            String currentAreaTypeIdentifier = areaTypesIdentifiers[level];
            String areaName = areasContainer.areaName;

            System.err.println("Currently analyzing area \"" + areaName + "\" of type \"" + currentAreaTypeIdentifier + "\".");
            if(!canBeValidArea(areaName, areaTypesIdentifiers)) {
                System.err.println("Error: area with name \"" + areaName + "\" of type \"" + currentAreaTypeIdentifier + "\" is not a valid name.");
                return false;
            }

            System.err.println("Area \"" + areaName + "\" is correct.");
            for(var area : areasContainer.areas) {
                if(!isAreasContainerCorrect(area, areaTypesIdentifiers, level + 1)) {
                    System.err.println("Error: area with name \"" + areaName + "\" of type \"" + currentAreaTypeIdentifier + "\" is not a valid area.");
                    return false;
                }
            }

            return isMainLocationFieldCorrect(areasContainer.mainLocation);
        } else {
            // It's actually a locations container (each field in areasContainer is a location object).
            return isLocationsContainerCorrect(areasContainer, areaTypesIdentifiers, level);
        }
    }

    private static boolean isLocationsContainerCorrect(Area locationsContainer, String[] areaTypesIdentifiers, int level) {
        String locationName = locationsContainer.areaName;
        OpenFaaSRedisConfiguration locationConfiguration = locationsContainer.mainLocation;
        //for(const locationName in locationsContainer) {
            //if(locationName === "main-location") {
            //    continue;
            //}
            System.err.println("Currently analyzing location: " + locationName + ".");
            if(!canBeValidArea(locationName, areaTypesIdentifiers)) {
                System.err.println("Error: location with name \"" + locationName + "\" is not a valid name.");
                return false;
            }
            if(!canBeValidLocation(locationConfiguration)) {
                System.err.println("Error: location with name \"" + locationName + "\" is not a valid location (the location must contain all the required fields).");
                return false;
            }
            System.err.println("Location \"" + locationName + "\" is correct.");
        //}
        if(level == 0)
            return true; // hierarchy field does not need the main-location field.
        else
            return isMainLocationFieldCorrect(locationConfiguration);
    }



    private static boolean isMainLocationFieldCorrect(OpenFaaSRedisConfiguration mainLocationObject) {
        if(canBeValidLocation(mainLocationObject)) {
            return true;
        } else {
            System.err.println("Error: main-location is not a valid location (the location must contain all the required fields).");
            return false;
        }
    }



    private static boolean isAUniqueAreaName(String areaName) {
        boolean isUnique = !areaNames.contains(areaName); // It is unique if the array of names does not contain the name.
        if (isUnique)
            areaNames.add(areaName);
        return isUnique;
    }

    private static boolean canBeValidArea(String areaName, String[] areaTypesIdentifiers) {
        if(!isAUniqueAreaName(areaName)) {
            System.err.println("Error: area names must be unique. There are more than one area with name \"" + areaName + "\".");
            return false;
        } else if(!canBeValidAreaName(areaName, areaTypesIdentifiers)) {
            System.err.println("Error: area with name \"" + areaName + "\" is not a valid name.");
            return false;
        } else {
            return true;
        }
    }





    private static boolean canBeValidAreaName(String areaName, String[] areaTypesIdentifiers) {
        return !reservedKeywords.contains(areaName) &&
                !new ArrayList<>(List.of(areaTypesIdentifiers)).contains(areaName);
    }

    private static boolean canBeValidLocation(OpenFaaSRedisConfiguration location) {
        if(location == null) {
            System.err.println("Error: the location is undefined.");
            return false;
        } else if(!(
                location.openfaas_gateway != null && !location.openfaas_gateway.isEmpty() &&
                location.openfaas_password != null && !location.openfaas_password.isEmpty() &&
                location.redis_host != null && !location.redis_host.isEmpty() &&
                location.redis_password != null && !location.redis_password.isEmpty())) {
            System.err.println("Error: the location has one or more missing fields.");
            return false;
        } else {
            return true;
        }
    }
}
