package cli;

import cli.commands.CheckInfrastructure;
import cli.commands.Deploy;
import cli.commands.DisplayInfrastructure;

import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String helpString = """
Available commands:

check-infrastructure <path to infrastructure file>

display-infrastructure <path to infrastructure file>

deploy <function name> <path to infrastructure file>
    --inEvery <areaTypeIdentifier>: In which area type to deploy the function. If not specified the function is deployed to the lowest level.
    --inAreas <area>: The name of the areas in which to deploy the function. If not specified the function is deployed everywhere.
    --exceptIn <area>: The name of the areas in which to NOT deploy the function.
    -f, --yaml <path>: Path to the YAML file describing the function.
    --minReplicas <number_of_replicas>: Number of replicas for the function. Default: 2
    --receivePropagate: Set this function to be a receiver of propagate() calls.
    
help
""";
        if (args.length == 0)
        {
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
                String pathToFunction = "./stack.yml";
                String minReplicas = "5";
                boolean receivePropagate = false;

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
                            case "-f", "--yaml" -> {
                                pathToFunction = args[i + 1];
                                i += 2;
                            }
                            case "--minReplicas" -> {
                                minReplicas = args[i + 1];
                                i += 2;
                            }
                            case "--receivePropagate" -> {
                                receivePropagate = true;
                                i += 1;
                            }
                            default -> {
                                System.err.println("Not sure what '" + args[i] + "' refers to.\n" + helpString);
                                i += 1;
                            }
                        }
                } catch (IndexOutOfBoundsException ignored) { }

                Deploy.deploy(
                        functionName,
                        infrastructure,
                        inEvery,
                        inAreas.toArray(new String[0]),
                        exceptIn.toArray(new String[0]),
                        pathToFunction,
                        minReplicas,
                        receivePropagate
                );
            }
            case "help" -> System.out.println(helpString);
            default -> System.err.println("Unrecognized command.\n" + helpString);
        }
    }

}
