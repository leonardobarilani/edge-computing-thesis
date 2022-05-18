package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.utils.EdgeInfrastructureUtils;
import com.openfaas.function.common.utils.HTTPUtils;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.function.common.utils.MigrateUtils;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

import java.io.IOException;

public class OffloadSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler();

        String offloading = new RedisHandler(RedisHandler.OFFLOAD).get("offloading");
        if (offloading.equals("accept"))
        {
            System.out.println("Offloading accepted");
            // offload accepted

            String migrateFrom;

            // update json object
            SessionToken session = new SessionToken();
            session.initJson(req.getBody());
            migrateFrom = session.currentLocation;
            session.currentLocation = System.getenv("LOCATION_ID");
            String jsonNewSession = new Gson().toJson(session);

            // save new json object in redis
            redis.set(session.session, jsonNewSession);

            // send new json object to proprietaryLocation
            String url = EdgeInfrastructureUtils.getGateway(session.proprietaryLocation) +
                    "/function/session-offloading-manager?command=update-session";
            System.out.println("Updating proprietary:\n\t" + url + "\n\t" + jsonNewSession);
            try {
                HTTPUtils.sendAsyncJsonPOST(url, jsonNewSession);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // migrate data from the current location where the session data is to this node
            String location = EdgeInfrastructureUtils.getGateway(migrateFrom);
            String sessionToMigrate = session.session;
            System.out.println("Migrating session from:\n\t" + location);
            MigrateUtils.callRemoteMigrate(location, sessionToMigrate);
        }
        else
        {
            System.out.println("Offloading not accepted");
            // offload redirected to parent

            // call parent node to offload the session
            String url = EdgeInfrastructureUtils.getParentHost() +
                    "/function/session-offloading-manager?command=offload-session";
            System.out.println("Redirecting session to parent:\n\t" + url + "\n\t" + req.getBody());
            try {
                HTTPUtils.sendAsyncJsonPOST(url, req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        redis.close();
    }
}
