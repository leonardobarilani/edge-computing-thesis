package com.openfaas.function.command;

import com.openfaas.function.common.EdgeInfrastructureUtils;
import com.openfaas.function.common.HTTPUtils;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.function.common.infrastucture.Infrastructure;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CallFunction implements ICommand {

    @Override
    public void Handle(IRequest req, IResponse res) {
        String functionSession = req.getHeader("X-function-session");

        System.out.println("Header X-function-session: " + functionSession);

        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS);

        String sessionJson = redis.get(functionSession);

        if (sessionJson == null)
        {
            // the session is not present (probably onloaded)

            res.setHeader("X-Function-Result", "failure");

            String message = "Session <" + functionSession + "> not present";
            res.setBody(message);
            System.out.println(message);
        }
        else
        {
            SessionToken sessionToken = new SessionToken();
            sessionToken.initJson(sessionJson);

            if (sessionToken.currentLocation.equals(System.getenv("LOCATION_ID")))
            {
                // the session is stored locally, I can call the actual function

                String uri = EdgeInfrastructureUtils.getGateway(System.getenv("LOCATION_ID")) + "/function/" + sessionToken.function;
                System.out.println("Calling on:\n\t" + uri + "\n\tBody: " + req.getBody());
                HttpResponse response = null;
                try {
                    /*response = HTTPUtils.sendAsyncJsonPOST(
                            uri,
                            req.getBody()
                    ).get();*/
                    HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                            .header("X-function-session", functionSession)
                            .POST(HttpRequest.BodyPublishers.ofString(req.getBody()))
                            .build();

                    response = HttpClient.newHttpClient()
                            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                            .get();

                    System.out.println(
                        "Received response: "
                        +"\n\tURI: "+response.uri()
                        +"\n\tHeaders: "+response.headers()
                        +"\n\tStatus: "+response.statusCode()
                        +"\n\tBody: <"+response.body() + ">"
                    );

                    res.setBody((String) response.body());
                    res.setHeader("X-Function-Result", "success");
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    res.setBody("Internal server error");
                    res.setStatusCode(500);
                }

                System.out.println("Executed session <" + functionSession + ">");
            }
            else
            {
                // the session is offloaded, I have to redirect the client

                res.setHeader("X-Function-Result", "offload");
                res.setHeader("X-Offload", EdgeInfrastructureUtils.getGateway(sessionToken.currentLocation));

                String message = "Session <" + functionSession + "> is offloaded to location <" + sessionToken.currentLocation + "> (" + EdgeInfrastructureUtils.getGateway(sessionToken.currentLocation) + ")";
                res.setBody(message);
                System.out.println(message);
            }
        }
    }
}
