package com.openfaas.function.commands;

import com.openfaas.function.commands.wrappers.Response;
import com.openfaas.function.commands.wrappers.WrapperGarbageCollector;
import com.openfaas.function.commands.wrappers.WrapperMigrateSession;
import com.openfaas.function.daos.*;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.function.utils.Logger;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;

import java.util.HashSet;
import java.util.List;

public class GarbageCollector implements ICommand {

    HashSet<SessionToken> sessions;

    public void Handle(IRequest req, IResponse res) {
        String deletePolicy = "expiration";

        if (req.getQuery().get("deletePolicy") != null)
            deletePolicy = req.getQuery().get("deletePolicy");
        else {
            Logger.log("No deletePolicy specified, assuming expiration deletePolicy");
        }

        String sessionId = req.getQuery().get("sessionId");
        if (deletePolicy.equals("forced")) {
            if (sessionId == null) {
                res.setBody("{" +
                        "statusCode:400," +
                        "message:\"Missing sessionId query parameter. Required by deletePolicy=forced\"" +
                        "}");
                res.setStatusCode(400);
                return;
            }
        }

        // returnValue == 0: no sessions deleted
        // returnValue > 0: number deleted sessions
        long returnValue = 0;

        populateData();

        Logger.log("Sessions count: " + sessions.size());

        switch (deletePolicy) {
            case "expiration":
                returnValue = deleteExpiredSessions();
                break;
            case "forced":
                if (SessionsDAO.getSessionToken(sessionId) == null) {
                    Logger.log("The session \"" + sessionId + "\" specified to be deleted using the \"forced\" deletePolicy does not exist");
                    returnValue = 0;
                } else {
                    if (SessionsLocksDAO.lockSession(sessionId)) {
                        deleteSession(sessionId);
                        returnValue = 1;
                        SessionsLocksDAO.unlockSession(sessionId);
                    }
                }
                break;
            default:
                Logger.log("Unrecognized deletePolicy: " + deletePolicy);
        }
        Logger.log("Deleted sessions count: " + returnValue);

        res.setBody(Long.toString(returnValue));
        res.setStatusCode(200);
    }

    private void populateData() {
        sessions = new HashSet<>();

        List<String> sessionsKeys = SessionsDataDAO.getAllSessionsIds();

        for (var sessionId : sessionsKeys) {
            SessionToken session = SessionsDAO.getSessionToken(sessionId);
            sessions.add(session);
        }
    }

    private long deleteExpiredSessions() {
        long deletedSessionsCount = 0;
        for (SessionToken session : sessions) {
            // if the session is offloaded and we are the proprietary of it, we can't collect it
            if (!(!System.getenv("LOCATION_ID").equals(session.currentLocation) &&
                System.getenv("LOCATION_ID").equals(session.proprietaryLocation))) {
                int elapsedSeconds = elapsedSeconds(session.timestampLastAccess);
                if (elapsedSeconds > ConfigurationDAO.getSessionsDataExpirationTime()) {
                    if (SessionsLocksDAO.lockSession(session.session)) {
                        deleteSession(session.session);
                        SessionsLocksDAO.unlockSession(session.session);
                        deletedSessionsCount++;
                    }
                }
            }
        }
        return deletedSessionsCount;
    }

    private int elapsedSeconds (String past) {
        DateTime dateTime = DateTime.parse(past, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC());
        DateTime now = DateTime.now(DateTimeZone.UTC);
        int seconds = Seconds.secondsBetween(dateTime, now).getSeconds();
        return seconds;
    }

    private void deleteSession (String session) {
        // if we are not the proprietary of the session we are deleting,
        // we have to ping the proprietary to tell it to delete the session
        String proprietaryLocation = SessionsDAO.getSessionToken(session).proprietaryLocation;
        if (!System.getenv("LOCATION_ID").equals(proprietaryLocation)) {
            Response responseSessionData = new Response("", 0);
            while(responseSessionData.getStatusCode() != 200) {
                responseSessionData = new WrapperGarbageCollector()
                        .gateway(EdgeInfrastructureUtils.getGateway(proprietaryLocation))
                        .sessionToDelete(session)
                        .call();
                try {
                    Thread.sleep(1000);
                } catch(Exception e) {}
            }
        }

        SessionsDAO.deleteSessionToken(session);
        SessionsDataDAO.deleteSessionData(session);
        SessionsRequestsDAO.deleteSessionRequest(session);
    }
}
