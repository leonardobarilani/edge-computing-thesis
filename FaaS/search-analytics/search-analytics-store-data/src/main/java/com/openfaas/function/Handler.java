package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

import java.util.Calendar;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();

        System.out.println("------------BEGIN SEARCH STORE DATA------------");

        // Current time in minutes
        String currentLocalDatabase =
                Long.toString(Calendar.getInstance().getTimeInMillis() / (1000L * 60L));

        long ttl = 60L * 60L * 4L; // 4 hours
        ttl = 60L * 5L; // 5 minutes
        EdgeDB db = new EdgeDB(currentLocalDatabase);
        db.addToList("searches_list", req.getBody());
        db.setTTL(ttl);
        db.close();

        res.setStatusCode(200);
        System.out.println("------------END SEARCH STORE DATA------------");
        return res;
    }
}
