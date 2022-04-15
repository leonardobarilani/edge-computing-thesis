package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.EdgeInfrastructureUtils;
import com.openfaas.function.common.HTTPUtils;
import com.openfaas.function.common.JedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

public class ForceUnload implements Command {

    public void Handle(IRequest req, IResponse res) {
        JedisHandler redis = new JedisHandler();

        // call parent node to receive a session
        String url = EdgeInfrastructureUtils.getParentHost() +
                "/function/session-offloading-manager?command=unload-session";
        System.out.println("Unloading from:\n\t" + url);
        String sessionJson = HTTPUtils.sendGET(url, "").body;

        // update json object
        SessionToken session = new Gson().fromJson(sessionJson, SessionToken.class);
        session.currentLocation = System.getenv("LOCATION_ID");
        String jsonNewSession = new Gson().toJson(session);

        // save new json object in redis
        redis.set(session.session, jsonNewSession);

        // send new json object to proprietaryLocation
        String urlLeaf = session.proprietaryLocation +
                "/session-offloading-manager?command=update-session";
        System.out.println("Updating proprietary:\n\t" + urlLeaf + "\n\t" + jsonNewSession);
        HTTPUtils.sendGET(urlLeaf, jsonNewSession);

        res.setStatusCode(200);
        res.setBody("Unloaded:\n\tOld session: " + sessionJson + "\n\tNew session: " + jsonNewSession);
    }
}
