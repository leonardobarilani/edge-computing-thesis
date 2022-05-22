package com.openfaas.function.api;

import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.function.common.utils.EdgeInfrastructureUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

public abstract class Offloadable extends com.openfaas.model.AbstractHandler {

    public IResponse Handle (IRequest req) {
        System.out.println("\n\n\n--------BEGIN OFFLOADABLE--------");
        IResponse res;
        String sessionId = req.getHeader("X-session");
        assert sessionId != null;
        SessionToken sessionToken;

        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS);
        String sessionJson = redis.get(sessionId);
        redis.close();

        System.out.println("(Offloadable) About to locate session <" + sessionId + ">...");

        if (sessionJson == null)
        {
            // We are in the proprietary location, we create the session

            System.out.println("(Offloadable) The session doesn't exists. About to create a new session, sessionId: " + sessionId);
            sessionToken = new SessionToken();
            sessionToken.init(sessionId);
            sessionJson = sessionToken.getJson();
            System.out.println("(Offloadable) New session created: \n\t"+ sessionJson);

            redis = new RedisHandler(RedisHandler.SESSIONS);
            redis.set(sessionToken.session, sessionJson);
            redis.close();

            System.out.println("(Offloadable) Session saved in Redis");

            res = HandleOffload(req);
        }
        else
        {
            System.out.println("(Offloadable) Session exists. Detecting if locally or offloaded...");

            sessionToken = new SessionToken();
            sessionToken.initJson(sessionJson);

            if (!sessionToken.currentLocation.equals(System.getenv("LOCATION_ID"))) {
                /*  currentLocation doesn't match with this location,
                    we have to perform a redirect
                 */
                System.out.println("(Offloadable) Session exists but it is offloaded. About to redirect the request...");

                String redirectUrl =
                        EdgeInfrastructureUtils.getGateway(sessionToken.currentLocation) +
                                "/function/" +
                                System.getenv("FUNCTION_NAME") + "?" +
                                req.getQueryRaw();

                System.out.println("(Offloadable) Redirecting session <" + sessionJson + "> to: " + redirectUrl);

                res = new Response();
                res.setStatusCode(307);
                res.setHeader("Location", redirectUrl);
            }
            else
            {
                // session exist and it is in this location

                System.out.println("(Offloadable) Session exists and it is here. About to handle the request...");
                res = HandleOffload(req);
            }
        }

        System.out.println("--------END OFFLOADABLE--------");
        return res;
    }

    public abstract IResponse HandleOffload (IRequest req) ;
}