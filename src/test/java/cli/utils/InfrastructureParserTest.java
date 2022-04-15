package cli.utils;

import com.google.gson.Gson;
import cli.infrastucture.Infrastructure;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InfrastructureParserTest {

    @Test
    public void testGson () {
        String path = "src/main/resources/new-infrastructure.json";
        Gson g = new Gson();
        Infrastructure infrastructure;
        try {
            infrastructure = g.fromJson(Files.readString(Path.of(path)), Infrastructure.class);

            assert infrastructure.areaTypesIdentifiers[0].equals("continent");
            assert infrastructure.areaTypesIdentifiers[1].equals("country");
            assert infrastructure.areaTypesIdentifiers[2].equals("city");
            assert infrastructure.areaTypesIdentifiers[3].equals("district");

            assert infrastructure.hierarchy[0].areaName.equals("europe");
            assert infrastructure.hierarchy[0].mainLocation.openfaas_gateway.equals("http://10.211.55.10:31112");
            assert infrastructure.hierarchy[0].mainLocation.redis_port == 6379;
            assert infrastructure.hierarchy[0].areas.length == 2;

            //                   europe,      france,  nice,    nice001, no areas
            assert infrastructure.hierarchy[0].areas[1].areas[1].areas[1].areas.length == 0;

            // location_ids are assigned dynamically (if we'll even use them)
            assert infrastructure.hierarchy[0].areas[1].areas[1].areas[1].mainLocation.location_id == null;
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testCheckInfrastructure (){
        String fileName = "src/main/resources/new-infrastructure.json";
        Gson g = new Gson();
        Infrastructure infrastructure;
        try {
            infrastructure = g.fromJson(Files.readString(Path.of(fileName)), Infrastructure.class);
            assert InfrastructureParser.isInfrastructureJsonCorrect(infrastructure);
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }
}