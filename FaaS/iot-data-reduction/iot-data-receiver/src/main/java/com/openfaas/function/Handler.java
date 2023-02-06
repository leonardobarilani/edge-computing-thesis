package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.*;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        System.out.println("\n\n----------BEGIN IOT DATA RECEIVER----------");

        int separator = req.getBody().indexOf(":");
        String device = req.getBody().substring(0, separator);
        String newValue = req.getBody().substring(separator + 1);

        EdgeDB db = new EdgeDB("latest_data:" + device);

        String oldValue = db.get("latest_iot_data");

        db.set("latest_iot_data", newValue);
        db.close();

        System.out.println("Location: " + System.getenv("LOCATION_ID"));
        System.out.println("IoT Device: " + device);
        System.out.println("Change in value: " + oldValue + " -> " + newValue);

        res.setStatusCode(200);
        System.out.println("----------END IOT DATA RECEIVER----------");
        return res;
    }
}
