package main.commands;

import com.google.gson.Gson;
import main.infrastucture.Area;
import main.infrastucture.Infrastructure;
import main.infrastucture.OpenFaaSRedisConfiguration;
import main.utils.InfrastructureParser;
import main.utils.LocationsGetter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Deploy {

    // infrastructureFileName was infrastructure
    public static void deploy(String functionName, String infrastructureFileName,
                              String inEvery, String[] inAreas, String[] exceptIn, String yaml)
    {
        Gson g = new Gson();
        Infrastructure infrastructure = null;
        String infrastructureString = null;
        try {
            infrastructureString = Files.readString(Path.of(infrastructureFileName));
            infrastructure = g.fromJson(infrastructureString, Infrastructure.class);

            // Check correctness of infrastructure file.
            System.out.println("🔄 Checking if infrastructure is correct.");
            if (!InfrastructureParser.isInfrastructureJsonCorrect(infrastructure)) {
                System.err.println("❌ The infrastructure JSON is NOT correct.");
                return;
            }
            System.out.println("✅ The infrastructure JSON is correct.");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // TODO:
        // Check correctness of input.
        /*console.log(chalk.white.bold("🔄 Checking if input is correct."));
        if(!infrastructureParser.isDeploymentInputCorrect(infrastructureJson, inEvery, inAreas, exceptIn)) {
            console.log(chalk.red.bold("❌ The input is NOT correct."));
            return;
        }
        console.log(chalk.green.bold("✅ The input is correct."));*/

        // Get locations to deploy.
        System.out.println("🔄 Getting all locations of infrastructure.");
        Area[] listOfLocations = LocationsGetter.getAllLocations(infrastructure, inEvery, inAreas, exceptIn);
        if (listOfLocations.length == 0) {
            System.err.println("❌ The input does not correspond to any location.");
            return;
        }

        // Detect system to use correct shell.
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String shellPreamble;
        if (isWindows)
            shellPreamble = "cmd.exe /c ";
        else
            shellPreamble = "sh -c ";

        // Deploy to all locations.
        for (Area location : listOfLocations) {
            OpenFaaSRedisConfiguration conf = location.mainLocation;
            String envVariablesString =
                "--env=LOCATION_ID=" + location.areaName +
                " --env=EDGE_DEPLOYMENT_IN_EVERY=" + inEvery +
                " --env=EDGE_INFRASTRUCTURE='" + infrastructureString + "'" +
                " --env=REDIS_HOST=" + conf.redis_host +
                " --env=REDIS_PORT=" + conf.redis_port +
                " --env=REDIS_PASSWORD=" + conf.redis_password;
            System.out.println(
                    "📶 Deploying on location: \"" + location.areaName +
                            "\", gateway: \"" + conf.openfaas_gateway + "\".");

            Process proc;
            String command;
            try {
                command =
                    "echo " + conf.openfaas_password +
                    " | faas-cli login " + 
                    "--username admin " +
                    "--password-stdin " +
                    "--gateway " + conf.openfaas_gateway;
                proc = Runtime.getRuntime().exec(shellPreamble + command);
                printOutput(proc);

                command =
                    "faas-cli deploy --filter " + functionName +
                    " --yaml " + yaml +
                    " --gateway " + conf.openfaas_gateway + " " + envVariablesString;
                proc = Runtime.getRuntime().exec(shellPreamble + command);
                printOutput(proc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printOutput(Process proc) {
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));
        String s = null;
        try
        {
            while ((s = stdInput.readLine()) != null)
                System.err.println(s);
            while ((s = stdError.readLine()) != null)
                System.out.println(s);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
