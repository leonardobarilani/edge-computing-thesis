package cli.commands;

import cli.infrastucture.Area;
import cli.infrastucture.Infrastructure;
import cli.infrastucture.OpenFaaSRedisConfiguration;
import cli.utils.InfrastructureParser;
import cli.utils.LocationsGetter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public class Deploy {

    public static void deploy(String functionName,
                              String infrastructureFileName,
                              String inEvery,
                              String[] inAreas,
                              String[] exceptIn,
                              List<String> faasCliArguments) {
        Infrastructure infrastructure;
        String infrastructureString;
        try {
            infrastructureString = Files.readString(Path.of(infrastructureFileName));
            ObjectMapper mapper = new ObjectMapper();
            infrastructure = mapper.readValue(Files.readString(Path.of(infrastructureString)), Infrastructure.class);
            infrastructure.autoFill();
            infrastructureString = mapper.writeValueAsString(infrastructure);

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
            shellPreamble = "";

        //todo deploy configuration as a secretmap

        // Deploy to all locations.
        for (Area location : listOfLocations) {
            OpenFaaSRedisConfiguration conf = location.mainLocation;
            String infrastructureBase64 = Base64.getEncoder().encodeToString(infrastructureString.getBytes());
            String envVariablesString =
                    " --env=LOCATION_ID=" + location.areaName +
                            " --env=EDGE_DEPLOYMENT_IN_EVERY=" + inEvery +
                            " --env=EDGE_INFRASTRUCTURE=" + infrastructureBase64 +
                            " --env=REDIS_HOST=" + conf.redis_host +
                            " --env=REDIS_PORT=" + conf.redis_port +
                            " --env=REDIS_PASSWORD=" + conf.redis_password +
                            " --env=FUNCTION_NAME=" + functionName;
            System.out.println(
                    "📶 Deploying on location: \"" + location.areaName +
                            "\", gateway: \"" + conf.openfaas_gateway + "\".");

            Process proc;
            String command;
            try {
                command =
                        " faas-cli login " +
                                " --username admin " +
                                " --password " + conf.openfaas_password +
                                " --gateway " + conf.openfaas_gateway;
                System.out.println("Executing: " + shellPreamble + command);
                proc = Runtime.getRuntime().exec(shellPreamble + command);
                printOutput(proc);

                command =
                        " faas-cli deploy --filter " + functionName +
                                " --gateway " + conf.openfaas_gateway +
                                " " + envVariablesString +
                                " " + String.join(" ", faasCliArguments);
                System.out.println("Executing: " + shellPreamble + command);
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
        String s;
        try {
            while ((s = stdInput.readLine()) != null)
                System.err.println(s);
            while ((s = stdError.readLine()) != null)
                System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
