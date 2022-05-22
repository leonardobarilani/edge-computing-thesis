package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

public class UpdateSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS);

        SessionToken sessionToken = new Gson().fromJson(req.getBody(), SessionToken.class);
        String sessionJson = req.getBody();

        if (!sessionToken.proprietaryLocation.equals(System.getenv("LOCATION_ID")))
        {
            // the session doesn't belong in this leaf

            String message = "Trying to update-session on the wrong leaf:\n\t" +
                    System.getenv("LOCATION_ID") + "\n\t" +
                    sessionJson;
            System.out.println(message);

            res.setBody(message);
            res.setStatusCode(400);
        }
        else if (redis.get(sessionToken.session).isEmpty())
        {
            // the session doesn't exist

            String message = "The session doesn't exist:\n\t" +
                    sessionJson;
            System.out.println(message);

            res.setBody(message);
            res.setStatusCode(400);
        }
        else
        {
            // the session gets updated

            String oldSession = redis.get(sessionToken.session);
            
            redis.set(sessionToken.session, sessionJson);

            String message = "Session updated:\n\t" +
                    oldSession + " -> " + sessionJson;

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(200);
        }
        redis.close();
    }
}
