package com.openfaas.function.command;

import com.openfaas.function.common.EdgeInfrastructureUtils;
import com.openfaas.function.common.HTTPUtils;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

import java.io.IOException;

public class ForceOffload implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS);

        // get a random session to offload
        SessionToken randomSession = new SessionToken();
        String json = redis.getRandom();

        if (json == null)
        {
            System.out.println("Node is empty, can't force an offload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an offload");
        }
        else
        {
            randomSession.initJson(json);

            // remove the local reference of the session if it is not our session
            if (!randomSession.proprietaryLocation.equals(randomSession.currentLocation))
                redis.delete(randomSession.session);

            // call parent node to offload the session
            String url = EdgeInfrastructureUtils.getParentHost() +
                    "/function/session-offloading-manager?command=offload-session";
            String offloadedSession = randomSession.getJson();
            String message = "Offloading:\n\t" + url + "\n\t" + offloadedSession;

            try {
                HTTPUtils.sendAsyncJsonPOST(url, offloadedSession);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(message);

            res.setStatusCode(200);
            res.setBody(message);
        }
        redis.close();
    }
}
