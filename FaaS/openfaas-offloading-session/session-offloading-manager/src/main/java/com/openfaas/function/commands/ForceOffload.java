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

        /* --------- Checks before using the session --------- */
        sessionToOffload = SessionsDAO.getSessionToken(forcedSessionId);
        if (!sessionExists(res, sessionToOffload))
            return;
        if (!acquireLock(res, forcedSessionId))
            return;

        if (sessionToOffload == null) {
            System.out.println("Node is empty, can't force an offload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an offload");
        } else {
            System.out.println("Session token about to be offloaded: " + sessionToOffload.getJson());
        }
        /* --------- Offload --------- */
        offloadSession(res, sessionToOffload);

        /* --------- Release session --------- */
        releaseLock(res, forcedSessionId);
    }

    private void offloadSession (IResponse res, SessionToken sessionToOffload) {
        System.out.println("Session token about to be offloaded: " + sessionToOffload.getJson());

        // call parent node to offload the session
        String message = "Offloading:\n\t" + EdgeInfrastructureUtils.getParentLocationId() + "\n\t" + sessionToOffload.getJson();
        new WrapperOffloadSession()
                .gateway(EdgeInfrastructureUtils.getParentHost())
                .sessionToOffload(sessionToOffload)
                .call();

        // if we are not the proprietary of the session, we have to
        // set the currentLocation to proprietaryLocation so that the proprietary
        // will properly redirect to the actual currentLocation
        if (!sessionToOffload.proprietaryLocation.equals(System.getenv("LOCATION_ID"))) {
            sessionToOffload.currentLocation = sessionToOffload.proprietaryLocation;
            SessionsDAO.setSessionToken(sessionToOffload);
        }

        System.out.println(message);
        res.setStatusCode(200);
        res.setBody(message);
    }

    private boolean acquireLock (IResponse res, String session) {
        if (!SessionsDAO.lockSession(session))
        {
            System.out.println("Cannot acquire lock on session <" + session + ">");
            res.setStatusCode(400);
            res.setBody("Cannot acquire lock on session <" + session + ">");
            return false;
        }
        return true;
    }

    private boolean releaseLock (IResponse res, String session) {
        if (!SessionsDAO.unlockSession(session))
        {
            System.out.println("Cannot release lock on session <" + session + ">");
            res.setStatusCode(500);
            res.setBody("Cannot release lock on session <" + session + ">");
            return false;
        }
        return true;
    }

    private boolean sessionExists (IResponse res, SessionToken session) {
        if (session == null)
        {
            System.out.println("Node is empty, can't force an offload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an offload");
            return false;
        }
        return true;
    }
}
