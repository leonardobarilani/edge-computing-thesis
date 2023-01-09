package com.openfaas.function.api;

import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

public abstract class Offloadable extends com.openfaas.model.AbstractHandler {

    public IResponse Handle (IRequest req) {
        IResponse res = null;
        try {
            System.out.println("\n\n\n--------BEGIN OFFLOADABLE--------");
            String sessionId = req.getHeader("X-session");
            System.out.println("(Offloadable) X-session: " + sessionId);
            if (sessionId == null)
                System.out.println("(Offloadable) [Warning] X-session is null, it shouldn't happen");

            SessionToken sessionToken = SessionsDAO.getSessionToken(sessionId);;

            System.out.println("(Offloadable) About to locate session <" + sessionId + ">...");

            if (sessionToken == null) {
                // We are in the proprietary location, we create the session

                System.out.println("(Offloadable) The session doesn't exists. About to create a new session, sessionId: " + sessionId);
                sessionToken = new SessionToken();
                sessionToken.init(sessionId);
                System.out.println("(Offloadable) New session created: \n\t" + sessionToken.getJson());

                SessionsDAO.setSessionToken(sessionToken);

                System.out.println("(Offloadable) Session saved in Redis");

                res = HandleOffload(req);
            } else {
                System.out.println("(Offloadable) Session exists. Detecting if locally or offloaded...");

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

                    System.out.println("(Offloadable) Redirecting session <" + sessionToken.getJson() + "> to: " + redirectUrl);

                    res = new Response();
                    res.setStatusCode(307);
                    res.setHeader("Location", redirectUrl);
                } else {
                    // session exist and it is in this location

                    System.out.println("(Offloadable) Session exists and it is local. About to handle the request...");
                    res = HandleOffload(req);
                }
            }

            System.out.println("--------END OFFLOADABLE--------");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public abstract IResponse HandleOffload (IRequest req) ;
}
