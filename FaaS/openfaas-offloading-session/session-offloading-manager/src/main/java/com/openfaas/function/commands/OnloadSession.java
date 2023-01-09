package com.openfaas.function.commands;

import com.openfaas.function.daos.RedisHandler;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

import java.util.List;

/**
 * onload-session API:
 *  Header X-onload-location: location that requested the onload
 */
public class OnloadSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        // X-onload-location header must be present
        String onloadLocation = req.getHeader("X-onload-location");
        if (onloadLocation == null)
        {
            System.out.println("Missing X-onload-location header");
            res.setStatusCode(400);
            res.setBody("Missing X-onload-location header");
            return;
        }

        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS);

        // FIXME
        // Fix onload-session bug (example: A with children B and C. B offload to A.
        // C call onload on A. A onload session of node B to node C)
        // Use EdgeInfrastructures.getLocationsSubTree(request.sender)

        // find a session that can be onloaded
        // (the proprietaryLocation has to be a sub node of the onloadingLocation)
        SessionToken onloadedSession = null;
        List<String> subNodes = EdgeInfrastructureUtils.getLocationsSubTree(onloadLocation);
        RedisHandler sessionData = new RedisHandler(RedisHandler.SESSIONS_DATA);
        List<String> localSessionsIds = sessionData.getAllSessionsIds();
        sessionData.close();
        for (var session : localSessionsIds)
        {
            SessionToken token = new SessionToken();
            token.initJson(redis.get(session));
            if (subNodes.contains(token.proprietaryLocation))
            {
                onloadedSession = token;
                break;
            }
        }

        if (onloadedSession == null)
        {
            System.out.println("Node is empty, can't force an unload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an unload");
        }
        else
        {
            String onloadedSessionJson = onloadedSession.getJson();
            System.out.println("Onloading:\n\t" + onloadedSessionJson);

            res.setStatusCode(200);
            res.setBody(onloadedSessionJson);

            // to allow a correct redirect from this node to the node that actually has the session,
            // we redirect to the proprietary that will finally redirect to the correct node
            onloadedSession.currentLocation = onloadedSession.proprietaryLocation;
            redis.set(onloadedSession.session, onloadedSession.getJson());
        }
        redis.close();
    }
}
