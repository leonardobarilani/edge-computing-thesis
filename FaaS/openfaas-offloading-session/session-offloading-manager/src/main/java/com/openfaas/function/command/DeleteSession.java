package com.openfaas.function.command;

import com.openfaas.function.common.EdgeInfrastructureUtils;
import com.openfaas.function.common.HTTPUtils;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DeleteSession implements Command {

    public void Handle(IRequest req, IResponse res) {
        String sessionToDelete = req.getQuery().get("session");
        RedisHandler redis = new RedisHandler();

        if (sessionToDelete == null || sessionToDelete.isEmpty())
        {
            System.out.println("Received a malformed request");
            // malformed request

            res.setBody("Please specify a valid session: /session-offloading-manager?command=delete-session&session=<session_id>");
            res.setStatusCode(400);
        }
        else {
            String sessionValue = redis.get(sessionToDelete);
            if (sessionValue == null)
            {
                System.out.println("Specified session doesn't exist");
                // specified session doesn't exist

                res.setBody("Session <" + sessionToDelete + "> not found on " + System.getenv("LOCATION_ID"));
                res.setStatusCode(400);
            }
            else
            {
                SessionToken sessionToken = new SessionToken();
                sessionToken.initJson(sessionValue);

                CompletableFuture<HttpResponse<String>> future = null;
                // delete the session remotely (if it is offloaded, and I am the proprietary)
                if (!sessionToken.currentLocation.equals(sessionToken.proprietaryLocation) &&
                    sessionToken.proprietaryLocation.equals(System.getenv("LOCATION_ID"))) {
                    System.out.println("Deleting the session remotely (" + sessionToken.currentLocation + ")");
                    try {
                        future = HTTPUtils.sendAsyncJsonPOST(
                                EdgeInfrastructureUtils.getGateway(sessionToken.currentLocation) +
                                        "/function/session-offloading-manager?command=delete-session&session=" + sessionToken.session,
                                ""
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("Deleting the session locally");
                // delete the session locally
                redis.delete(sessionToDelete);


                if (future != null)
                {
                    try {
                        if (future.get().statusCode() != 200)
                        {
                            res.setBody("Session <" + sessionToDelete + "> unsuccessfully deleted remotely. Remote response ("+future.get().statusCode()+"): " + future.get().body());
                            res.setStatusCode(future.get().statusCode());
                        }
                        else
                        {
                            res.setBody("Session <" + sessionToDelete + "> successfully deleted remotely and locally");
                            res.setStatusCode(200);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    res.setBody("Session <" + sessionToDelete + "> successfully deleted");
                    res.setStatusCode(200);
                }
            }
        }
        redis.close();
    }
}
