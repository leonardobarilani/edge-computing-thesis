package main;

import main.commands.CheckInfrastructure;
import main.commands.Deploy;
import main.commands.DisplayInfrastructure;

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

                for(int i = 3;i < args.length - 1;i += 2)
                    switch (args[i])
                    {
                        case "--inEvery" -> inEvery = args[i + 1];
                        case "--inAreas" -> inAreas.add(args[i + 1]);
                        case "--exceptIn" -> exceptIn.add(args[i + 1]);
                        case "-f" -> pathToFunction = args[i + 1];
                        case "--yaml" -> pathToFunction = args[i + 1];
                        default -> System.err.println("Not sure what '" + args[i] + "' refers to.\n" + helpString);
                    }

                Deploy.deploy(
                        functionName,
                        infrastructure,
                        inEvery,
                        inAreas.toArray(new String[0]),
                        exceptIn.toArray(new String[0]),
                        pathToFunction
                );
            }
            case "help" -> System.out.println(helpString);
            default -> System.err.println("Unrecognized command.\n" + helpString);
        }
    }

}