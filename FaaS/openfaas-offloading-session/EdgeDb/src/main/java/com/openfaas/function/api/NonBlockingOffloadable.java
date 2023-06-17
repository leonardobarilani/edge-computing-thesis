package com.openfaas.function.api;

import com.openfaas.function.commands.services.ForceOffloadService;
import com.openfaas.function.daos.ConfigurationDAO;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.daos.SessionsRequestsDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.function.utils.Logger;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

public abstract class NonBlockingOffloadable extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        Logger.log("\n\n\n--------BEGIN NON BLOCKING OFFLOADABLE--------");
        Logger.log("Queries:");
        for (var v : req.getQuery().keySet())
            Logger.log("\t" + v + ":\t" + req.getQuery().get(v));
        Logger.log("Headers:");
        for (var v : req.getHeaders().keySet())
            Logger.log("\t" + v + ":\t" + req.getHeader(v));
        try {
            String sessionId = checkSessionHeader(req, res);
            if (sessionId != null) {
                String requestId = checkRequestIdHeader(req, res);
                if (requestId != null) {
                    Logger.log("(NonBlockingOffloadable) About to locate session <" + sessionId + ">...");
                    SessionToken sessionToken = SessionsDAO.getSessionToken(sessionId);
                    if (sessionToken == null) {
                        Logger.log("(NonBlockingOffloadable) Session does not exists. Creating new session with sessionId <" + sessionId + ">");
                        // We are in the proprietary location, we create the session
                        res = handleNewSession(req, sessionId, requestId);
                    } else {
                        Logger.log("(NonBlockingOffloadable) Session exists. Detecting if locally or offloaded...");
                        if (!sessionToken.currentLocation.equals(System.getenv("LOCATION_ID"))) {
                            // CurrentLocation doesn't match with this location, we have to perform a redirect
                            Logger.log("(NonBlockingOffloadable) Session exists but it is offloaded. About to redirect the request...");
                            res = handleRemoteSession(req, sessionToken);
                        } else {
                            // Session exist and it is in this location
                            Logger.log("(NonBlockingOffloadable) Session exists and it is local. About to handle the request...");
                            res = handleLocalSession(req, sessionId, requestId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String stackTrace = sw.toString();
            String message = "500 Internal server error\n Stack trace: " + stackTrace;
            Logger.log(message);
            res.setBody(message);
            res.setStatusCode(500);
        }
        Logger.log("--------END NON BLOCKING OFFLOADABLE--------");
        return res;
    }

    public abstract IResponse HandleNonBlockingOffload(IRequest req);

    private String checkSessionHeader(IRequest req, IResponse res) {
        String sessionId = req.getHeader("X-session");
        Logger.log("(NonBlockingOffloadable) X-session: " + sessionId);
        if (sessionId == null) {
            Logger.log("(NonBlockingOffloadable) X-session is null, sending 300");
            res.setStatusCode(300);
            res.setBody("300 Header X-session is not present");
        }
        return sessionId;
    }

    private String checkRequestIdHeader(IRequest req, IResponse res) {
        String requestId = req.getHeader("X-session-request-id");
        Logger.log("(NonBlockingOffloadable) X-session-request-id: " + requestId);
        if (requestId == null) {
            Logger.log("(NonBlockingOffloadable) X-session-request-id is null, sending 300");
            res.setStatusCode(300);
            res.setBody("300 Header X-session-request-id is not present");
        } else {
            Pattern UUID_REGEX =
                    Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

            if (!UUID_REGEX.matcher(requestId).matches()) {
                Logger.log("(NonBlockingOffloadable) X-session-request-id <" + requestId + "> is not a UUID string, sending 300");
                res.setStatusCode(300);
                res.setBody("300 Header X-session-request-id <" + requestId + "> is not a UUID string");
                requestId = null;
            }
        }
        return requestId;
    }

    private boolean checkRequestIdUniqueness(String sessionId, String requestId, IResponse res) {
        if (SessionsRequestsDAO.existsSessionRequest(sessionId, requestId)) {
            Logger.log("(NonBlockingOffloadable) X-session-request-id was already processed, sending 208");
            res.setStatusCode(208);
            res.setBody("208 Header X-session-request-id was already processed");
            return true;
        }
        return false;
    }

    private IResponse handleNewSession(IRequest req, String sessionId, String requestId) {
        IResponse res = new Response();

        SessionToken sessionToken = new SessionToken();
        sessionToken.init(sessionId);
        Logger.log("(NonBlockingOffloadable) New session created: \n\t" + sessionToken.getJson());

        SessionsDAO.setSessionToken(sessionToken);
        Logger.log("(NonBlockingOffloadable) Session saved in Redis");

        if (ConfigurationDAO.getOffloading().equals("accept")) {
            res = handle(req, sessionId, requestId);
        } else {
            Logger.log("(NonBlockingOffloadable) Node is at full capacity, offloading the new session");
            new ForceOffloadService().Handle(res, sessionId);
            res = handleRemoteSession(req, sessionToken);
        }

        return res;
    }

    private IResponse handleRemoteSession(IRequest req, SessionToken sessionToken) {
        String redirectUrl =
                EdgeInfrastructureUtils.getGateway(sessionToken.currentLocation) +
                        "/function/" +
                        System.getenv("FUNCTION_NAME") + "?" +
                        req.getQueryRaw();

        Logger.log("(NonBlockingOffloadable) Redirecting session <" + sessionToken.getJson() + "> to: " + redirectUrl);

        Response res = new Response();
        res.setStatusCode(307);
        res.setBody("307 Session is remote. Location: " + redirectUrl);
        res.setHeader("Location", redirectUrl);
        return res;
    }

    private IResponse handleLocalSession(IRequest req, String sessionId, String requestId) {
        IResponse res;
        res = handle(req, sessionId, requestId);
        return res;
    }

    private IResponse handle(IRequest req, String sessionId, String requestId) {
        IResponse res = new Response();
        if (!checkRequestIdUniqueness(sessionId, requestId, res)) {
            EdgeDB.setCurrentSession(sessionId);
            res = HandleNonBlockingOffload(req);
            // The access time is updated regardless of a successful update of the data
            SessionsDAO.updateAccessTimestampToNow(sessionId);
        }
        return res;
    }
}
