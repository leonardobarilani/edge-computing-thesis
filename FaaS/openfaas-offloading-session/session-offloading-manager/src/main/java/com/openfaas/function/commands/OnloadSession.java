package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresHeaderAnnotation;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.daos.SessionsDataDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

import java.util.List;

/**
 * onload-session API:
 * Header X-onload-location: location that requested the onload
 */
@RequiresHeaderAnnotation.RequiresHeader(header = "X-onload-location")
public class OnloadSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        String onloadLocation = req.getHeader("X-onload-location");

        /* --------- Checks before using the session --------- */
        SessionToken onloadedSession = findOnloadableSession(res, onloadLocation);
        if (!sessionExists(res, onloadedSession))
            return;

        /* --------- Onload --------- */
        onloadSession(res, onloadedSession);

        /* --------- Release session --------- */
        releaseLock(res, onloadedSession.session);
    }

    private void onloadSession(IResponse res, SessionToken onloadedSession) {
        String onloadedSessionJson = onloadedSession.getJson();
        System.out.println("Onloading:\n\t" + onloadedSessionJson);

        res.setStatusCode(200);
        res.setBody(onloadedSessionJson);

        // to allow a correct redirect from this node to the node that actually has the session,
        // we redirect to the proprietary that will finally redirect to the correct node
        onloadedSession.currentLocation = onloadedSession.proprietaryLocation;
        SessionsDAO.setSessionToken(onloadedSession);
    }

    private SessionToken findOnloadableSession (IResponse res, String location) {
        // FIXME
        // Fix onload-session bug (example: A with children B and C. B offload to A.
        // C call onload on A. A onload session of node B to node C)
        // Use EdgeInfrastructures.getLocationsSubTree(request.sender)

        // find a session that can be onloaded
        // (the proprietaryLocation has to be a sub node of the onloadingLocation)
        SessionToken onloadedSession = null;
        List<String> subNodes = EdgeInfrastructureUtils.getLocationsSubTree(location);
        List<String> localSessionsIds = SessionsDataDAO.getAllSessionsIds();
        for (var session : localSessionsIds) {
            if (!SessionsDAO.lockSession(session))
                continue;
            SessionToken token = SessionsDAO.getSessionToken(session);
            if (subNodes.contains(token.proprietaryLocation)) {
                onloadedSession = token;
                break;
            }
            SessionsDAO.unlockSession(session);
        }

        if (onloadedSession == null) {
            System.out.println("Node is empty, can't force an unload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an unload");
        } else {
            String onloadedSessionJson = onloadedSession.getJson();
            System.out.println("Onloading:\n\t" + onloadedSessionJson);

            res.setStatusCode(200);
            res.setBody(onloadedSessionJson);
        }
        return onloadedSession;
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
