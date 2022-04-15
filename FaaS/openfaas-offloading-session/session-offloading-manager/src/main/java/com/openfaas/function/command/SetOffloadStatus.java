package com.openfaas.function.command;

import com.openfaas.function.common.JedisHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

public class SetOffloadStatus implements Command {

    public void Handle(IRequest req, IResponse res) {
        JedisHandler redis = new JedisHandler();

        String offloading = req.getQuery().get("status");

        if (!offloading.equals("yes") && !offloading.equals("no"))
        {
            String message = "Malformed request: <" + offloading + "> is not a valid offloading status (valid offloading statuses: yes/no)";

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
    }
}
