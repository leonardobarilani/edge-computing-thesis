package cli.utils;

import com.google.gson.Gson;
import cli.infrastucture.Area;
import cli.infrastucture.Infrastructure;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LocationsGetterTest {

    @Test
    public void testGetAllLocations (){
        String fileName = "src/main/resources/new-infrastructure.json";
        Gson g = new Gson();
        Infrastructure infrastructure;
        try {
            infrastructure = g.fromJson(Files.readString(Path.of(fileName)), Infrastructure.class);

            Area[] result = LocationsGetter.getAllLocations(
                infrastructure,
                "city",
                new String[]{"europe"},
                new String[]{"france"}
            );
            assert result.length == 2;
            assert result[0].areaName.equals("milan");
            assert result[1].areaName.equals("turin");

            result = LocationsGetter.getAllLocations(
                infrastructure,
                "district",
                new String[]{"france"},
                new String[]{"paris"}
            );
            assert result.length == 2;
            assert result[0].areaName.equals("nice001");
            assert result[1].areaName.equals("nice002");

            result = LocationsGetter.getAllLocations(
                    infrastructure,
                    "continent",
                    new String[]{"europe"},
                    new String[]{}
            );
            assert result.length == 1;
            assert result[0].areaName.equals("europe");
        } catch (IOException e) {
            e.printStackTrace();
            assert false;
        }
    }
}