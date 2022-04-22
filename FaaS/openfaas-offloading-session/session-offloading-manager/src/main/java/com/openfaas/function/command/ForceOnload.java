package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.EdgeInfrastructureUtils;
import com.openfaas.function.common.HTTPUtils;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

import java.io.IOException;

public class ForceOnload implements Command {

    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler();

        // call parent node to receive a session
        String url = EdgeInfrastructureUtils.getParentHost() +
                "/function/session-offloading-manager?command=onload-session";
        System.out.println("Onloading from:\n\t" + url);
        String sessionJson = HTTPUtils.sendGET(url, "").body;

        // update json object
        SessionToken session = new Gson().fromJson(sessionJson, SessionToken.class);
        session.currentLocation = System.getenv("LOCATION_ID");
        String jsonNewSession = new Gson().toJson(session);

        // save new json object in redis
        redis.set(session.session, jsonNewSession);

        // send new json object to proprietaryLocation
        String urlLeaf = EdgeInfrastructureUtils.getGateway(session.proprietaryLocation) +
                "/function/session-offloading-manager?command=update-session";
        System.out.println("Updating proprietary:\n\t" + urlLeaf + "\n\t" + jsonNewSession);
        //HTTPUtils.sendGETWithoutResponse(urlLeaf, jsonNewSession);
        try {
            HTTPUtils.sendAsyncJsonPOST(urlLeaf, jsonNewSession);
        } catch (IOException e) {
            e.printStackTrace();
        }

        res.setStatusCode(200);
        res.setBody("Unloaded:\n\tOld session: " + sessionJson + "\n\tNew session: " + jsonNewSession);

        redis.close();
    }
}
