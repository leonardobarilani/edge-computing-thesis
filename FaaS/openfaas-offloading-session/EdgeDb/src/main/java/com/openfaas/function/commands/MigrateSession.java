package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresQueryAnnotation;
import com.openfaas.function.daos.SessionsDataDAO;
import com.openfaas.function.daos.SessionsRequestsDAO;
import com.openfaas.function.model.sessiondata.SessionData;
import com.openfaas.function.utils.Logger;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

@RequiresQueryAnnotation.RequiresQuery(query = "session")
@RequiresQueryAnnotation.RequiresQuery(query = "data-type")
public class MigrateSession implements ICommand {

    @Override
    public void Handle(IRequest req, IResponse res) {

        String sessionId = req.getQuery().get("session");
        String dataType = req.getQuery().get("data-type");

        Logger.log("About to migrate Session Id: " + sessionId);

        if (dataType.equals("sessionData")) {
            Logger.log("Migrating session data");

            SessionData data = SessionsDataDAO.getSessionData(sessionId);

            res.setBody(data.toJSON());
            res.setStatusCode(200);

            SessionsDataDAO.deleteSessionData(sessionId);
        } else if (dataType.equals("requestIds")) {
            Logger.log("Migrating session request ids");

            String data = SessionsRequestsDAO.getSessionRequests(sessionId).toString();
            data = data.substring(1, data.length() - 1);
            data = data.replaceAll(" ", "");

            res.setBody(data);
            res.setStatusCode(200);

            SessionsRequestsDAO.deleteSessionRequest(sessionId);
        } else {
            String message = "Data-type <" + dataType + "> not recognized";
            Logger.log(message);
            res.setBody(message);
            res.setStatusCode(400);
        }
    }
}
