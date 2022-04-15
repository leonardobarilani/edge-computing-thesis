package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.EdgeInfrastructureUtils;
import com.openfaas.function.common.HTTPUtils;
import com.openfaas.function.common.JedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

public class ForceOffload implements Command {

    public void Handle(IRequest req, IResponse res) {
        JedisHandler redis = new JedisHandler();

        // get a random session to offload
        SessionToken randomSession = new Gson().fromJson(redis.getRandomSession(), SessionToken.class);

        if (randomSession == null)
        {
            System.out.println("Node is empty, can't force an offload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an offload");
        }
        else
        {
            // remove the local reference of the session
            redis.set(randomSession.session, null);

            // call parent node to offload the session
            String url = EdgeInfrastructureUtils.getParentHost() +
                    "/function/session-offloading-manager?command=receive-session";
            String offloadedSession = new Gson().toJson(new SessionToken[]{randomSession});
            System.out.println("Offloading:\n\t" + url + "\n\t" + offloadedSession);
            HTTPUtils.sendGET(url, offloadedSession);

            res.setStatusCode(200);
            res.setBody("Offloading:\n\t" + url + "\n\t" + offloadedSession);
        }
    }
}
