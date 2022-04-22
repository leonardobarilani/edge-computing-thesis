package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.EdgeInfrastructureUtils;
import com.openfaas.function.common.HTTPUtils;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

import java.io.IOException;

public class OffloadSession implements Command {

    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler();

        String offloading = new RedisHandler(RedisHandler.OFFLOAD).get("offloading");
        if (offloading.equals("accept"))
        {
            System.out.println("Offloading accepted");
            // offload accepted

            // update json object
            SessionToken session = new SessionToken();
            session.initJson(req.getBody());
            session.currentLocation = System.getenv("LOCATION_ID");
            String jsonNewSession = new Gson().toJson(session);

            // save new json object in redis
            redis.set(session.session, jsonNewSession);

            // send new json object to proprietaryLocation
            String url = EdgeInfrastructureUtils.getGateway(session.proprietaryLocation) +
                    "/function/session-offloading-manager?command=update-session";
            System.out.println("Updating proprietary:\n\t" + url + "\n\t" + jsonNewSession);
            //HTTPUtils.sendGETWithoutResponse(url, jsonNewSession);
            try {
                HTTPUtils.sendAsyncJsonPOST(url, jsonNewSession);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Offloading not accepted");
            // offload redirected to parent

            // call parent node to offload the session
            String url = EdgeInfrastructureUtils.getParentHost() +
                    "/function/session-offloading-manager?command=offload-session";
            System.out.println("Redirecting session to parent:\n\t" + url + "\n\t" + req.getBody());
            HTTPUtils.sendGETWithoutResponse(url, req.getBody());
            /*try {
                HTTPUtils.sendAsyncJsonPOST(url, req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        redis.close();
    }
}
