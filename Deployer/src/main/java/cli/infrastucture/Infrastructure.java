package cli.infrastucture;

import java.io.IOException;

public class Infrastructure {

    public String[] areaTypesIdentifiers;
    public Area[] hierarchy;

    public void autoFill () {
        try {
            autoFill(hierarchy[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void autoFill (Area area) throws IOException {
        area.mainLocation.autoFillMissing(area.areaName);
        for (var a : area.areas)
            autoFill(a);
    }
}
