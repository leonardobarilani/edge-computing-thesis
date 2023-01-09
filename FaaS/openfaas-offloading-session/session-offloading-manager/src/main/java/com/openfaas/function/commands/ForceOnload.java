package com.openfaas.function.commands;

import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.function.utils.MigrateUtils;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class ForceOnload implements ICommand {

    public void Handle(IRequest req, IResponse res) {

        // TODO wrap this into a cycle to iterate until we reached the root or exit if we onloaded something
        // while {

        // call parent node to receive a session
        String url = EdgeInfrastructureUtils.getParentHost() +
                "/function/session-offloading-manager?command=onload-session";
        System.out.println("Onloading from:\n\t" + url);

        HttpRequest request = HttpRequest
                .newBuilder(URI.create(url))
                .GET()
                .header("X-onload-location", System.getenv("LOCATION_ID"))
                .build();
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

        // } end while

        if (response.statusCode() != 200)
        {
            res.setStatusCode(400);
            System.out.println("/onload-session unable to provide a valid session");
            res.setBody("/onload-session unable to provide a valid session");
            return;
        }

        String sessionJson = response.body();
        String jsonNewSession = MigrateUtils.migrateSession(sessionJson);

        res.setStatusCode(200);
        res.setBody("Unloaded:\n\tOld session: " + sessionJson + "\n\tNew session: " + jsonNewSession);
    }
}
