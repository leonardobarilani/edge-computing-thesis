package com.openfaas.function.api;

import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.function.common.utils.EdgeInfrastructureUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

import java.net.URI;

public abstract class Offloadable extends OffloadableWrapper {

    IResponse HandleWrapper (IRequest req) {
        IResponse res;
        String sessionId = req.getHeader("X-session");
        assert sessionId != null;
        SessionToken sessionToken;

        RedisHandler redis = new RedisHandler();
        String sessionJson = redis.get(sessionId);
        redis.close();

        if (sessionJson == null)
        {
            // We are in the proprietary node, we create the session

            sessionToken = new SessionToken();
            sessionToken.init(sessionId);
            sessionJson = sessionToken.getJson();
            System.out.println("(Offloadable) New session created: \n\t"+ sessionJson);

            redis = new RedisHandler();
            redis.set(sessionToken.session, sessionJson);
            redis.close();

            System.out.println("(Offloadable) Session saved in Redis");

            res = Handle(req);
        }
        else
        {
            sessionToken = new SessionToken();
            sessionToken.initJson(sessionJson);

            if (!sessionToken.currentLocation.equals(System.getenv("LOCATION_ID"))) {
                /*  currentLocation doesn't match with this location,
                    we have to perform a redirect
                 */

                String redirectUrl =
                        EdgeInfrastructureUtils.getGateway(sessionToken.currentLocation) +
                        req.getPathRaw() +
                        req.getQueryRaw();

                System.out.println("(Offloadable) Redirecting session <" + sessionJson + "> to: " + redirectUrl);

                res = new Response();
                res.setStatusCode(307);
                res.setHeader("Location", redirectUrl);

            } else // session exist and it is here
            {
                res = Handle(req);
            }
        }

        return res;
    }

    public abstract IResponse Handle(IRequest req) ;
}

abstract class OffloadableWrapper extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        return HandleWrapper(req);
    }

    abstract IResponse HandleWrapper(IRequest req) ;
}
