package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresQueryAnnotation;
import com.openfaas.function.daos.SessionsDataDAO;
import com.openfaas.function.model.sessiondata.SessionData;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

@RequiresQueryAnnotation.RequiresQuery(query = "session")
public class MigrateSession implements ICommand {

    @Override
    public void Handle(IRequest req, IResponse res) {

        String sessionId = req.getQuery().get("session");

        System.out.println("About to migrate Session Id: " + sessionId);

        SessionData data = SessionsDataDAO.getSessionData(sessionId);

        res.setBody(data.toJSON());
        res.setStatusCode(200);

        SessionsDataDAO.deleteSessionData(sessionId);
    }
}
