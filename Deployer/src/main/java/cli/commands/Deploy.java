package cli.commands;

import cli.infrastucture.Area;
import cli.infrastucture.Infrastructure;
import cli.infrastucture.OpenFaaSRedisConfiguration;
import cli.utils.InfrastructureParser;
import cli.utils.LocationsGetter;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
                              boolean isReceivePropagate,
                              List<String> faasCliArguments) {
        Gson g = new Gson();
        Infrastructure infrastructure;
        String infrastructureString;
        try {
            infrastructureString = Files.readString(Path.of(infrastructureFileName));
            infrastructure = g.fromJson(infrastructureString, Infrastructure.class);
            infrastructure.autoFill();
            infrastructureString = g.toJson(infrastructure);

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

            // If it is a receivePropagate function I have to register it in
            if (isReceivePropagate) {
                String url = conf.openfaas_gateway + "/function/session-offloading-manager?command=register-receive-propagate&function=" + functionName;
                HttpRequest request = HttpRequest.newBuilder(URI.create(
                        url
                )).GET().build();

                System.out.println("Registering function at: " + url);

                try {
                    var res = HttpClient.newHttpClient()
                            .send(request, HttpResponse.BodyHandlers.ofString());
                    if (res.statusCode() == 200)
                        System.out.println("Function correctly registered as ReceivingFunction");
                    else
                        System.out.println("Unable to register function as ReceivingFunction: " +
                                "\nResponse code: " + res.statusCode() +
                                "\nResponse body: " + res.body());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
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
