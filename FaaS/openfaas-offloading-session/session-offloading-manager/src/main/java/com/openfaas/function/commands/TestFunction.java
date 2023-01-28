package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresQueryAnnotation;
import com.openfaas.function.daos.ConfigurationDAO;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.daos.SessionsDataDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

@RequiresQueryAnnotation.RequiresQuery(query="session")
public class TestFunction implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        String sessionRequested = req.getQuery().get("session");

        System.out.println("About to test: " + sessionRequested);

        SessionToken sessionToken = SessionsDAO.getSessionToken(sessionRequested);

        if (sessionToken == null)
        {
            String message =
                    "Session <" + sessionRequested + "> doesn't exist\n" +
                    "Offloading status: " + ConfigurationDAO.getOffloading();

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
        }
        else
        {
            String message  =
                    "Session <" + sessionRequested + ">: " + sessionToken.getJson() + "\n" +
                    "Offloading status: " + ConfigurationDAO.getOffloading();

            if (sessionToken.currentLocation.equals(System.getenv("LOCATION_ID")))
            {
                message += "\nSession data: " + SessionsDataDAO.getSessionData(sessionToken.session).toJSON();
            }
            else
            {
                message += "\nSession data: <data_not_on_this_node>";
            }

            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(200);
        }
    }
}
