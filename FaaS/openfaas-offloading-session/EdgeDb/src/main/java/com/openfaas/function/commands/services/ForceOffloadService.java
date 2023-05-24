package com.openfaas.function.commands.services;

import com.openfaas.function.commands.wrappers.WrapperOffloadSession;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.function.utils.Logger;
import com.openfaas.model.IResponse;

public class ForceOffloadService {

    public void Handle(IResponse res, String forcedSessionId) {
        SessionToken sessionToOffload;

        /* --------- Checks before using the session --------- */
        if (!sessionExists(res, forcedSessionId))
            return;
        sessionToOffload = SessionsDAO.getSessionToken(forcedSessionId);

        /* --------- Offload --------- */
        offloadSession(res, sessionToOffload);
    }

    private void offloadSession(IResponse res, SessionToken sessionToOffload) {
        Logger.log("Session token about to be offloaded: " + sessionToOffload.getJson());

        // call parent node to offload the session
        String message = "Offloading:\n" +
                EdgeInfrastructureUtils.getParentLocationId(System.getenv("LOCATION_ID")) + "\n" +
                sessionToOffload.getJsonLocationsOnly();
        new WrapperOffloadSession()
                .gateway(EdgeInfrastructureUtils.getParentHost(System.getenv("LOCATION_ID")))
                .sessionToOffload(sessionToOffload)
                .call();

        // if we are not the proprietary of the session, we have to
        // set the currentLocation to proprietaryLocation so that the proprietary
        // will properly redirect to the actual currentLocation
        if (!sessionToOffload.proprietaryLocation.equals(System.getenv("LOCATION_ID"))) {
            sessionToOffload.currentLocation = sessionToOffload.proprietaryLocation;
            SessionsDAO.setSessionToken(sessionToOffload);
        }

        Logger.log(message);
        res.setStatusCode(200);
        res.setBody(message);
    }

    private boolean sessionExists(IResponse res, String session) {
        SessionToken sessionToken = SessionsDAO.getSessionToken(session);
        if (sessionToken == null) {
            Logger.log("Node is empty, can't force an offload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an offload");
            return false;
        }
        return true;
    }
}
