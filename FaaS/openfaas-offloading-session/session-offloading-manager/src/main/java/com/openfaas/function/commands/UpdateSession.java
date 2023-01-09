package com.openfaas.function.commands;

import com.google.gson.Gson;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

public class UpdateSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
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
        else if (SessionsDAO.getSessionToken(sessionToken.session) == null)
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

            SessionToken oldSession = SessionsDAO.getSessionToken(sessionToken.session);
            
            SessionsDAO.setSessionToken(new SessionToken().initJson(sessionJson));

            String message = "Session updated:\n\t" +
                    oldSession + " -> " + sessionJson;

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(200);
        }
    }
}
