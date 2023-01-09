package com.openfaas.function.commands;

import com.openfaas.function.daos.RedisHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

public class SetOffloadStatus implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler(RedisHandler.OFFLOAD);

        String offloading = req.getQuery().get("status");

        if (!offloading.equals("accept") && !offloading.equals("reject"))
        {
            String message = "Malformed request: <" + offloading + "> is not a valid offloading status (valid offloading statuses: accept/reject)";

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
        }
        else
        {
            String message = "Offloading status from <" + redis.get("offloading") + "> to <" + offloading + ">";
            redis.set("offloading", offloading);

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(200);
        }
        redis.close();
    }
}
