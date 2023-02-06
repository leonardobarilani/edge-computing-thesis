package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresHeaderAnnotation;
import com.openfaas.function.commands.wrappers.WrapperOffloadSession;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

/**
 * force-offload API:
 * Header X-forced-session: sessionId of the session to offload
 */
@RequiresHeaderAnnotation.RequiresHeader(header = "X-forced-session")
public class ForceOffload implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        String forcedSessionId = req.getHeader("X-forced-session");
        SessionToken sessionToOffload;

        System.out.println("Header X-forced-session: " + forcedSessionId);

        sessionToOffload = SessionsDAO.getSessionToken(forcedSessionId);

        if (sessionToOffload == null) {
            System.out.println("Node is empty, can't force an offload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an offload");
        } else {
            System.out.println("Session token about to be offloaded: " + sessionToOffload.getJson());

            // call parent node to offload the session
            String message = "Offloading:\n\t" + EdgeInfrastructureUtils.getParentLocationId() + "\n\t" + sessionToOffload.getJson();

            new WrapperOffloadSession()
                    .gateway(EdgeInfrastructureUtils.getParentHost())
                    .sessionToOffload(sessionToOffload)
                    .call();

            if (!sessionToOffload.proprietaryLocation.equals(System.getenv("LOCATION_ID"))) {
                // if we are not the proprietary of the session, we have to
                // set the currentLocation to proprietaryLocation so that the proprietary
                // will properly redirect to the actual currentLocation
                sessionToOffload.currentLocation = sessionToOffload.proprietaryLocation;
                SessionsDAO.setSessionToken(sessionToOffload);
            }

            System.out.println(message);

            res.setStatusCode(200);
            res.setBody(message);
        }
    }
}
