package com.openfaas.function.command;

import com.openfaas.function.common.JedisHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

public class TestFunction implements Command {

    public void Handle(IRequest req, IResponse res) {

        String sessionRequested = req.getQuery().get("session");

        System.out.println("About to test: " + sessionRequested);

        JedisHandler redis = new JedisHandler();
        String sessionJson = redis.get(sessionRequested);

        if (sessionJson.isEmpty())
        {
            String message =
                    "Session <" + sessionRequested + "> doesn't exist\n" +
                    "Offloading status: " + (redis.get("offloading").equals("yes") ? "yes" : "no");

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
        }
        else
        {
            String message =
                    "Session <" + sessionRequested + ">: " + sessionJson + "\n" +
                    "Offloading status: " + (redis.get("offloading").equals("yes") ? "yes" : "no");

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(200);
        }
    }
}
