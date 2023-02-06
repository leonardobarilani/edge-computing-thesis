package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.function.api.Offloadable;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

import java.util.Objects;

/*
 * iot-data-reducer API:
 * iot-data-reducer?value=<current-value>
 * */

public class Handler extends Offloadable {

    public IResponse HandleOffload(IRequest req) {
        IResponse res = new Response();
        System.out.println("--------BEGIN IOT DATA REDUCER--------");
        EdgeDB db = new EdgeDB(req);

        String device = req.getHeader("X-session");
        String newValue = req.getQuery().get("value");
        String oldValue = db.get("latest_iot_data");

        System.out.println("Location: " + System.getenv("LOCATION_ID"));
        System.out.println("IoT Device: " + device);

        if (!Objects.equals(newValue, oldValue)) {
            System.out.println("Detected change in value: " + oldValue + " -> " + newValue);
            System.out.println("About to propagate...");
            res.setBody("Value changed, propagating");

            db.set("latest_iot_data", newValue);
            db.propagate(device + ":" + newValue, "city", "iot-data-receiver");
        } else {
            res.setBody("No change in value, not propagating");
            System.out.println("No change in value detected, no propagation will be performed");
            System.out.println("Current value: " + oldValue);
        }
        db.close();

        System.out.println("--------END IOT DATA REDUCER--------");
        db.close();
        return res;
    }
}
