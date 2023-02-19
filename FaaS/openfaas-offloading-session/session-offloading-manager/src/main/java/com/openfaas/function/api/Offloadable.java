package com.openfaas.function.api;

import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.daos.SessionsLocksDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

public abstract class Offloadable extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        System.out.println("\n\n\n--------BEGIN OFFLOADABLE--------");
        try {
            String sessionId = checkSessionHeader(req, res);
            if (sessionId != null) {
                System.out.println("(Offloadable) About to locate session <" + sessionId + ">...");
                SessionToken sessionToken = SessionsDAO.getSessionToken(sessionId);
                if (sessionToken == null) {
                    System.out.println("(Offloadable) Session does not exists. Creating new session with sessionId <" + sessionId + ">");
                    // We are in the proprietary location, we create the session
                    res = handleNewSession(req, sessionId);
                } else {
                    System.out.println("(Offloadable) Session exists. Detecting if locally or offloaded...");
                    if (!sessionToken.currentLocation.equals(System.getenv("LOCATION_ID"))) {
                        // CurrentLocation doesn't match with this location, we have to perform a redirect
                        System.out.println("(Offloadable) Session exists but it is offloaded. About to redirect the request...");
                        res = handleRemoteSession(req, sessionToken);
                    } else {
                        // Session exist and it is in this location
                        System.out.println("(Offloadable) Session exists and it is local. About to handle the request...");
                        res = handleLocalSession(req, sessionId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("--------END OFFLOADABLE--------");
        return res;
    }

    public abstract IResponse HandleOffload(IRequest req);

    private String checkSessionHeader (IRequest req, IResponse res) {
        String sessionId = req.getHeader("X-session");
        System.out.println("(Offloadable) X-session: " + sessionId);
        if (sessionId == null) {
            System.out.println("(Offloadable) X-session is null, sending 300");
            res.setStatusCode(300);
            res.setBody("300 Header X-session is not present");
        }
        return sessionId;
    }
    
    private IResponse handleNewSession (IRequest req, String sessionId) {
        IResponse res;
        if (SessionsLocksDAO.lockSession(sessionId)) {
            SessionToken sessionToken = new SessionToken();
            sessionToken.init(sessionId);

            System.out.println("(Offloadable) New session created: \n\t" + sessionToken.getJson());

            SessionsDAO.setSessionToken(sessionToken);

            System.out.println("(Offloadable) Session saved in Redis");
            res = handle(req, sessionId);
        } else {
            System.out.println("(Offloadable) Session <" + sessionId + "> not available. Can't acquire the session's lock");
            res = new Response();
            res.setStatusCode(503);
            // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Retry-After
            res.setHeader("Retry-After", "5");
            res.setBody("503 Session <" + sessionId + "> not available");
        }
        return res;
    }
    
    private IResponse handleRemoteSession (IRequest req, SessionToken sessionToken) {
        String redirectUrl =
                EdgeInfrastructureUtils.getGateway(sessionToken.currentLocation) +
                        "/function/" +
                        System.getenv("FUNCTION_NAME") + "?" +
                        req.getQueryRaw();

        System.out.println("(Offloadable) Redirecting session <" + sessionToken.getJson() + "> to: " + redirectUrl);

        Response res = new Response();
        res.setStatusCode(307);
        res.setHeader("Location", redirectUrl);
        return res;
    }
    
    private IResponse handleLocalSession (IRequest req, String sessionId) {
        IResponse res;
        if (SessionsLocksDAO.lockSession(sessionId)) {
            res = handle(req, sessionId);
        } else {
            System.out.println("(Offloadable) Session <" + sessionId + "> not available. Can't acquire the session's lock");
            res = new Response();
            res.setStatusCode(503);
            // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Retry-After
            res.setHeader("Retry-After", "5");
            res.setBody("503 Session <" + sessionId + "> not available");
        }
        return res;
    }

    private IResponse handle(IRequest req, String sessionId) {
        IResponse res;
        EdgeDB.setCurrentSession(sessionId);
        res = HandleOffload(req);
        SessionsLocksDAO.unlockSessionAndUpdateData(sessionId, EdgeDB.getCache());
        return res;
    }
}
