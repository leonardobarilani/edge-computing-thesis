package com.openfaas.function.command;

import com.openfaas.function.common.RedisHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

public class TestFunction implements ICommand {

    public void Handle(IRequest req, IResponse res) {

        String sessionRequested = req.getQuery().get("session");
        if (sessionRequested == null)
            sessionRequested = "";

        System.out.println("About to test: " + sessionRequested);

        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS);
        String sessionJson = redis.get(sessionRequested);
        redis.close();
        redis = new RedisHandler(RedisHandler.OFFLOAD);

        if (sessionJson == null || sessionJson.isEmpty())
        {
            String message =
                    "Session <" + sessionRequested + "> doesn't exist\n" +
                    "Offloading status: " + (redis.get("offloading").equals("accept") ? "accept" : "reject");

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
        }
        else
        {
            String message =
                    "Session <" + sessionRequested + ">: " + sessionJson + "\n" +
                    "Offloading status: " + (redis.get("offloading").equals("accept") ? "accept" : "reject");

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(200);
        }
        redis.close();
    }
}
