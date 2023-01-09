package com.openfaas.function.commands;

import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.function.utils.HTTPUtils;
import com.openfaas.function.model.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

import java.io.IOException;

/**
 * force-offload API:
 *  Header X-forced-session: sessionId of the session to offload. If not present, a random session will be offloaded
 */
public class ForceOffload implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        String forcedSessionId = req.getHeader("X-forced-session");
        SessionToken sessionToOffload;

        System.out.println("Header X-forced-session: "+forcedSessionId);

        if (forcedSessionId != null)
            // get the forced session to offload
            sessionToOffload = SessionsDAO.getSessionToken(forcedSessionId);
        else
            // get a random session to offload
            sessionToOffload = SessionsDAO.getRandomSessionToken();

        if (sessionToOffload == null)
        {
            System.out.println("Node is empty, can't force an offload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an offload");
        }
        else
        {
            System.out.println("Session token about to be offloaded: " + sessionToOffload.getJson());

            // call parent node to offload the session
            String url = EdgeInfrastructureUtils.getParentHost() +
                    "/function/session-offloading-manager?command=offload-session";
            String offloadedSession = sessionToOffload.getJson();
            String message = "Offloading:\n\t" + url + "\n\t" + offloadedSession;

            try {
                HTTPUtils.sendAsyncJsonPOST(url, offloadedSession);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            // set the currentLocation to proprietaryLocation so that the proprietary
            // will properly redirect to the actual currentLocation
            sessionToOffload.currentLocation = sessionToOffload.proprietaryLocation;
            SessionsDAO.setSessionToken(sessionToOffload);

            System.out.println(message);

            res.setStatusCode(200);
            res.setBody(message);
        }
    }
}
