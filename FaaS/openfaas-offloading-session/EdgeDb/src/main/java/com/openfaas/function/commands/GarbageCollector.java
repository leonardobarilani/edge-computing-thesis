package com.openfaas.function.commands;

import com.openfaas.function.daos.*;
import com.openfaas.function.model.SessionToken;
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
        // returnValue == 0: no sessions deleted
        // returnValue > 0: number deleted sessions
        long returnValue = 0;

        populateData();

        Logger.log("Sessions count: " + sessions.size());

        returnValue = deleteExpiredSessions();

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
            int elapsedSeconds = elapsedSeconds(session.timestampLastAccess);
            if (elapsedSeconds > ConfigurationDAO.getSessionsDataExpirationTime()) {
                if (SessionsLocksDAO.lockSession(session.session)) {
                    SessionsDAO.deleteSessionToken(session.session);
                    SessionsDataDAO.deleteSessionData(session.session);
                    SessionsLocksDAO.unlockSession(session.session);
                    SessionsRequestsDAO.deleteSessionRequest(session.session);
                    deletedSessionsCount++;
                }
            }
        }
        return deletedSessionsCount;
    }

    private int elapsedSeconds(String past) {
        DateTime dateTime = DateTime.parse(past, DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC());
        DateTime now = DateTime.now(DateTimeZone.UTC);
        int seconds = Seconds.secondsBetween(dateTime, now).getSeconds();
        return seconds;
    }
}
