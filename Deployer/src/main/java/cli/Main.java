package cli;

import cli.commands.CheckInfrastructure;
import cli.commands.Deploy;
import cli.commands.DisplayInfrastructure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String helpString = """
                Available commands:

                check-infrastructure <path to infrastructure file>

                display-infrastructure <path to infrastructure file>

                deploy <function name> <path to infrastructure file>
                    --inEvery <areaTypeIdentifier>: In which area type to deploy the function.
                        If not specified the function is deployed to the lowest level.
                    --inAreas <area>: The name of the areas in which to deploy the function.
                        If not specified the function is deployed everywhere.
                    --exceptIn <area>: The name of the areas in which to NOT deploy the function.
                    --faas-cli <faas-cli deploy compatible parameter>: The argument of this parameter will be directly passed to faas-cli deploy.
                        See https://github.com/openfaas/faas-cli for more info.
                        You can specify this parameter multiple times.
                    
                help
                """;
        if (args.length == 0) {
            System.err.println("Unrecognized command specified.\n" + helpString);
            return;
        }
        switch (args[0]) {
            case "check-infrastructure" -> {
                if (args.length < 2) {
                    System.err.println("Missing parameter: <path to infrastructure file>.");
                    return;
                }
                CheckInfrastructure.checkInfrastructure(args[1]);
            }
            case "display-infrastructure" -> {
                if (args.length < 2) {
                    System.err.println("Missing parameter: <path to infrastructure file>.");
                    return;
                }
                DisplayInfrastructure.displayInfrastructure(args[1]);
            }
            case "deploy" -> {
                if (args.length < 3) {
                    System.err.println("Missing parameters: <function name> <path to infrastructure file>.\n" + helpString);
                    return;
                }

                String functionName = args[1];
                String infrastructure = args[2];
                String inEvery = "";
                List<String> inAreas = new LinkedList<>();
                List<String> exceptIn = new LinkedList<>();
                List<String> faasCliArguments = new ArrayList<>();

                int i = 3;
                try {
                    while (i < args.length)
                        switch (args[i]) {
                            case "--inEvery" -> {
                                inEvery = args[i + 1];
                                i += 2;
                            }
                            case "--inAreas" -> {
                                inAreas.add(args[i + 1]);
                                i += 2;
                            }
                            case "--exceptIn" -> {
                                exceptIn.add(args[i + 1]);
                                i += 2;
                            }
                            case "--faas-cli" -> {
                                faasCliArguments.add(args[i + 1]);
                                i += 2;
                            }
                            default -> {
                                System.err.println("Not sure what '" + args[i] + "' refers to.\n" + helpString);
                                i += 1;
                            }
                        }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Error parsing command line arguments");
                    e.printStackTrace();
                }

                Deploy.deploy(
                        functionName,
                        infrastructure,
                        inEvery,
                        inAreas.toArray(new String[0]),
                        exceptIn.toArray(new String[0]),
                        faasCliArguments
                );
            }
            case "help" -> System.out.println(helpString);
            default -> System.err.println("Unrecognized command.\n" + helpString);
        }
    }

}
