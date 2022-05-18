package com.openfaas.function;

import com.openfaas.function.common.RedisHandler;
import com.openfaas.model.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        try {

            System.out.println("\n\n----------BEGIN NEW COUNTER----------");
            System.out.println("Query raw: " + req.getQueryRaw());
            for (var v : req.getQuery().keySet())
                System.out.println("Key: " + v + ". Value: " + req.getQuery().get(v));
            System.out.println("Headers: " + req.getHeaders());

            String propagatedValue = req.getBody();
            System.out.println("0");
            RedisHandler db = new RedisHandler("4");
            System.out.println("1");

            String quantityString = db.get(propagatedValue);
            if (quantityString == null)
                quantityString = "0";
            System.out.println("2");

            int quantity = Integer.parseInt(quantityString) + 1;
            System.out.println("3");
            db.set(propagatedValue, Integer.toString(quantity));

            System.out.println(
                    "Someone just added <" + propagatedValue + "> to their cart.\n" +
                    "Current number of <" + propagatedValue + "> as counted by node <" + System.getenv("LOCATION_ID") + ">: " + quantity);
            res.setStatusCode(200);




            System.out.println("----------END NEW COUNTER----------");
        } catch(Exception e) { e.printStackTrace(); }
	    return res;
    }
}
