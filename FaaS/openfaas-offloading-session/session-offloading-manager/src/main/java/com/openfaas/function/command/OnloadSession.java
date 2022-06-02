package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

public class OnloadSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS);

        // FIXME
        // Fix onload-session bug (example: A with children B and C. B offload to A.
        // C call onload on A. A onload session of node B to node C)
        // Use EdgeInfrastructures.getLocationsSubTree(request.sender)

        // get a random session to onload
        SessionToken randomSession = new Gson().fromJson(redis.getRandom(), SessionToken.class);

        if (randomSession == null)
        {
            System.out.println("Node is empty, can't force an unload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an unload");
        }
        else
        {
            String unloadedSession = new Gson().toJson(randomSession);
            System.out.println("Onloading:\n\t" + unloadedSession);

            res.setStatusCode(200);
            res.setBody(unloadedSession);

            // to allow a correct redirect from this node to the node that actually has the session,
            // we redirect to the proprietary that will finally redirect to the correct node
            randomSession.currentLocation = randomSession.proprietaryLocation;
            redis.set(randomSession.session, new Gson().toJson(randomSession));
        }
        redis.close();
    }
}
