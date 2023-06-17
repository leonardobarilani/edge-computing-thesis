package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresHeaderAnnotation;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.Logger;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

import java.util.Base64;

@RequiresHeaderAnnotation.RequiresHeader(header = "X-session-token")
public class UpdateSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        String sessionJson = new String(Base64.getDecoder().decode(req.getHeader("X-session-token")));
        SessionToken sessionToken = SessionToken.Builder.buildFromJSON(sessionJson);

        if (!sessionToken.proprietaryLocation.equals(System.getenv("LOCATION_ID"))) {
            // the session doesn't belong in this leaf

            String message = "Trying to update-session on the wrong leaf:\n\t" +
                    System.getenv("LOCATION_ID") + "\n\t" +
                    sessionJson;
            Logger.log(message);

            res.setBody(message);
            res.setStatusCode(400);
        } else if (SessionsDAO.getSessionToken(sessionToken.session) == null) {
            // the session doesn't exist

            String message = "The session doesn't exist:\n\t" +
                    sessionJson;
            Logger.log(message);

            res.setBody(message);
            res.setStatusCode(400);
        } else {
            // the session gets updated

            SessionToken oldSession = SessionsDAO.getSessionToken(sessionToken.session);

            SessionsDAO.setSessionToken(SessionToken.Builder.buildFromJSON(sessionJson));

            String message = "Session updated:\n\t" +
                    oldSession.getJson() + " -> " + sessionJson;

            Logger.log(message);
            res.setBody(message);
            res.setStatusCode(200);
        }
    }
}
