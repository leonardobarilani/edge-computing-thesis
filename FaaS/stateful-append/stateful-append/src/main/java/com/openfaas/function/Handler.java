package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        System.out.println("---------BEGIN STATEFUL APPEND---------");
        System.out.println("Query raw: " + req.getQueryRaw());
        for (var v : req.getQuery().keySet())
            System.out.println("Key: " + v + ". Value: " + req.getQuery().get(v));
        System.out.println("Headers: " + req.getHeaders());

        EdgeDB redis = new EdgeDB(req);
        System.out.println("EdgeDB executed");
        String oldString = redis.get("string");
        System.out.println("Redis.get executed");
        if (oldString == null)
            oldString = "";

        String newString = oldString + req.getBody();
        System.out.println("Append executed");

        redis.set("string", newString);
        System.out.println("Redis.set executed");
	    res.setBody(newString);
        System.out.println("---------END STATEFUL APPEND---------");
	    return res;
    }
}
