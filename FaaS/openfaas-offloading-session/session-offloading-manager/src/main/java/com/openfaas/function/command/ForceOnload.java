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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class ForceOnload implements ICommand {

    public void Handle(IRequest req, IResponse res) {

        // call parent node to receive a session
        String url = EdgeInfrastructureUtils.getParentHost() +
                "/function/session-offloading-manager?command=onload-session";
        System.out.println("Onloading from:\n\t" + url);

        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();

            res.setStatusCode(500);
            System.out.println("Can't send get.");
            res.setBody("Can't send get.");
            return;
        }

        String sessionJson = response.body();

        if (response.statusCode() != 200)
        {
            res.setStatusCode(400);
            System.out.println("/onload-session unable to provide a valid session");
            res.setBody("/onload-session unable to provide a valid session");
            return;
        }

        // update json object
        SessionToken session = new Gson().fromJson(sessionJson, SessionToken.class);
        session.currentLocation = System.getenv("LOCATION_ID");
        String jsonNewSession = new Gson().toJson(session);

        // save new json object in redis
        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS);
        redis.set(session.session, jsonNewSession);
        redis.close();

        // send new json object to proprietaryLocation
        String urlLeaf = EdgeInfrastructureUtils.getGateway(session.proprietaryLocation) +
                "/function/session-offloading-manager?command=update-session";
        System.out.println("Updating proprietary:\n\t" + urlLeaf + "\n\t" + jsonNewSession);
        try {
            HTTPUtils.sendAsyncJsonPOST(urlLeaf, jsonNewSession);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // migrate data from the current location where the session data is to this node
        String location = EdgeInfrastructureUtils.getParentHost();
        String sessionToMigrate = session.session;
        System.out.println("Migrating session from:\n\t" + location);
        MigrateUtils.callRemoteMigrate(location, sessionToMigrate);

        res.setStatusCode(200);
        res.setBody("Unloaded:\n\tOld session: " + sessionJson + "\n\tNew session: " + jsonNewSession);
    }
}
