package cli.commands;

import cli.infrastucture.Infrastructure;
import cli.utils.InfrastructureParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CheckInfrastructure {

    public static void checkInfrastructure(String fileName) {
        Infrastructure infrastructure;
        try {
            infrastructure = new ObjectMapper().readValue(Files.readString(Path.of(fileName)), Infrastructure.class);

            // Check correctness of infrastructure file.
            System.err.println("🔄 Checking if infrastructure is correct.");
            if (!InfrastructureParser.isInfrastructureJsonCorrect(infrastructure)) {
                System.err.println("❌ The infrastructure JSON is NOT correct.");
                return;
            }
            System.err.println("✅ The infrastructure JSON is correct.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
