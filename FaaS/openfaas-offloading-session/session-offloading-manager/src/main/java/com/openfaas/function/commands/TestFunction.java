package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresQueryAnnotation;
import com.openfaas.function.daos.ConfigurationDAO;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.daos.SessionsDataDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

@RequiresQueryAnnotation.RequiresQuery(query = "type")
@RequiresQueryAnnotation.RequiresQuery(query = "value")
public class TestFunction implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        String typeRequested = req.getQuery().get("type");
        String valueRequested = req.getQuery().get("value");

        System.out.println("About to test: " + typeRequested + " with " + valueRequested);

        switch(typeRequested)
        {
            case "configuration":
                testConfiguration(res);
                break;
            case "sessionMetadata":
                testSessionMetadata(valueRequested, res);
                break;
            case "sessionData":
                testSessionData(valueRequested, res);
                break;
            case "session":
                testSession(valueRequested, res);
                break;
            default:
                testDefault(typeRequested, res);
        }
    }

    private void testConfiguration(IResponse res) {
        String message =
                "Offloading status: " + ConfigurationDAO.getOffloading();

        System.out.println(message);
        res.setBody(message);
        res.setStatusCode(200);
    }

    private void testSessionMetadata(String sessionId, IResponse res) {
        String message =
                "Session metadata <" + sessionId + ">: " + getSessionToken(sessionId) + "\n";

        System.out.println(message);
        res.setBody(message);
        res.setStatusCode(200);
    }

    private void testSessionData(String sessionId, IResponse res) {
        String message =
                "Session data <" + sessionId + ">: " + SessionsDataDAO.getSessionData(sessionId).toJSON() + "\n";

        System.out.println(message);
        res.setBody(message);
        res.setStatusCode(200);
    }

    private void testSession(String sessionId, IResponse res) {
        String message =
                "Session metadata <" + sessionId + ">: " + getSessionToken(sessionId) + "\n" +
                "Session data <" + sessionId + ">: " + SessionsDataDAO.getSessionData(sessionId).toJSON() + "\n";

        System.out.println(message);
        res.setBody(message);
        res.setStatusCode(200);
    }

    private void testDefault(String type, IResponse res) {
        String message = "Type <" + type + "> is not valid.\n";
        System.out.println(message);
        res.setBody(message);
        res.setStatusCode(400);
    }

    private String getSessionToken(String sessionId) {
        SessionToken token = SessionsDAO.getSessionToken(sessionId);
        String data;
        if (token == null) {
            return "<session_not_present_in_this_node>";
        } else {
            return token.getJson();
        }
    }
}
