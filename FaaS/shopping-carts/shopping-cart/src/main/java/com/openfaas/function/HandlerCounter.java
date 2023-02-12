package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

public class HandlerCounter {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        System.out.println("\n\n----------BEGIN COUNTER----------");

        String propagatedValue = req.getQuery().get("product");

        String quantityString = EdgeDB.get(propagatedValue);
        if (quantityString == null)
            quantityString = "0";

        int quantity = Integer.parseInt(quantityString) + 1;
        EdgeDB.set(propagatedValue, Integer.toString(quantity));

        System.out.println(
                "Someone just added <" + propagatedValue + "> to their cart.\n" +
                        "Current number of <" + propagatedValue + "> as counted by node <" + System.getenv("LOCATION_ID") + ">: " + quantity);

        res.setStatusCode(200);
        System.out.println("----------END COUNTER----------");
        return res;
    }
}
