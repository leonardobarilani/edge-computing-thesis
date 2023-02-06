package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.*;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        System.out.println("\n\n----------BEGIN COUNTER----------");

        String propagatedValue = req.getBody();

        EdgeDB db = new EdgeDB("counter");

        String quantityString = db.get(propagatedValue);
        if (quantityString == null)
            quantityString = "0";

        int quantity = Integer.parseInt(quantityString) + 1;
        db.set(propagatedValue, Integer.toString(quantity));
        db.close();

        System.out.println(
                "Someone just added <" + propagatedValue + "> to their cart.\n" +
                        "Current number of <" + propagatedValue + "> as counted by node <" + System.getenv("LOCATION_ID") + ">: " + quantity);

        res.setStatusCode(200);
        System.out.println("----------END COUNTER----------");
        return res;
    }
}
