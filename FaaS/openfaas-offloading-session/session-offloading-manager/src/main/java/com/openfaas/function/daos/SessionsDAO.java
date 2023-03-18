package com.openfaas.function.daos;

import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

import java.util.HashMap;

public class SessionsDAO extends RedisDAO {

    private static final SessionsDAO instance = new SessionsDAO();

    private SessionsDAO() {
        super(SESSIONS);
    }

    public static SessionToken getSessionToken(String sessionId) {
        SessionToken sessionToken = null;

        Logger.log("(SessionsDAO.getSessionToken) Trying to fetch session: " + sessionId);
        if (sessionId != null && instance.exists(sessionId) > 0) {
            sessionToken = new SessionToken();
            sessionToken.session = sessionId;
            sessionToken.proprietaryLocation = instance.hget(sessionId,
                    String.valueOf(SessionToken.Fields.PROPRIETARY_LOCATION));
            sessionToken.currentLocation = instance.hget(sessionId,
                    String.valueOf(SessionToken.Fields.CURRENT_LOCATION));
            sessionToken.timestampLastAccess = instance.hget(sessionId,
                    String.valueOf(SessionToken.Fields.TIMESTAMP_LAST_ACCESS));
            sessionToken.timestampCreation = instance.hget(sessionId,
                    String.valueOf(SessionToken.Fields.TIMESTAMP_CREATION));
            Logger.log("(SessionsDAO.getSessionToken) Session fetched from local storage: " + sessionToken.getJson());
        }
        return sessionToken;
    }

    public static void setSessionToken(SessionToken sessionToken) {
        HashMap<String, String> map = new HashMap<>();
        map.put(String.valueOf(SessionToken.Fields.PROPRIETARY_LOCATION), sessionToken.proprietaryLocation);
        map.put(String.valueOf(SessionToken.Fields.CURRENT_LOCATION), sessionToken.currentLocation);
        map.put(String.valueOf(SessionToken.Fields.TIMESTAMP_LAST_ACCESS), sessionToken.timestampLastAccess);
        map.put(String.valueOf(SessionToken.Fields.TIMESTAMP_CREATION), sessionToken.timestampCreation);
        instance.hset(sessionToken.session, map);
        Logger.log("(SessionsDAO.setSessionToken) Session saved to local storage: " + sessionToken.getJson());
    }

    public static void updateAccessTimestampToNow(String sessionId) {
        String now = ISODateTimeFormat.dateTimeNoMillis().print(DateTime.now(DateTimeZone.UTC));
        HashMap<String, String> map = new HashMap<>();
        map.put(String.valueOf(SessionToken.Fields.TIMESTAMP_LAST_ACCESS), now);
        instance.hset(sessionId, map);
        Logger.log("(SessionsDAO.updateAccessTimestampToNow) Updated last access time of session <" + sessionId + "> to <" + now + ">");
    }

    public static void deleteAllSessionTokens() {
        instance.deleteAll();
    }

    public static void deleteSessionToken(String sessionId) {
        instance.del(sessionId);
    }
}
