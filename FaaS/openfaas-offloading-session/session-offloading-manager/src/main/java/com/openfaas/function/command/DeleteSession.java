package com.openfaas.function.command;

import com.openfaas.function.common.HTTPUtils;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

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

                // delete the session remotely (if it is offloaded)
                if (!sessionToken.currentLocation.equals(sessionToken.proprietaryLocation)) {
                    System.out.println("Deleting the session remotely (" + sessionToken.currentLocation + ")");
                    HTTPUtils.sendGET(
                            sessionToken.currentLocation +
                                    "/session-offloading-manager?command=delete-session&session=" + sessionToken.session,
                            ""
                    );
                }

                System.out.println("Deleting the session locally");
                // delete the session locally
                redis.set(sessionToDelete, null);

                res.setBody("Session <" + sessionToDelete + "> successfully deleted");
                res.setStatusCode(200);
            }
        }
        redis.close();
    }
}
