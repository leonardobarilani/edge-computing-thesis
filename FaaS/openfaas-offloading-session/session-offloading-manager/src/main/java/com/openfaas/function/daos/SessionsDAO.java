package com.openfaas.function.daos;

import com.openfaas.function.model.SessionToken;

import java.util.HashMap;

public class SessionsDAO extends RedisDAO {

    private static final SessionsDAO instance = new SessionsDAO();

    private SessionsDAO() {
        super(SESSIONS);
    }

    public static SessionToken getSessionToken(String sessionId) {
        SessionToken sessionToken = null;

        System.out.println("(SessionsDAO.getSessionToken) Trying to fetch session: " + sessionId);
        if (sessionId != null && instance.exists(sessionId) > 0) {
            sessionToken = new SessionToken();
            sessionToken.session = sessionId;
            sessionToken.proprietaryLocation = instance.hget(sessionId,
                    String.valueOf(SessionToken.Fields.PROPRIETARY_LOCATION));
            if (sessionToken.proprietaryLocation == null)
                return null;
            sessionToken.currentLocation = instance.hget(sessionId,
                    String.valueOf(SessionToken.Fields.CURRENT_LOCATION));
            System.out.println("(SessionsDAO.getSessionToken) Session fetched from local storage: " + sessionToken.getJson());
        }
        return sessionToken;
    }

    public static void setSessionToken(SessionToken sessionToken) {
        HashMap<String, String> map = new HashMap<>();
        map.put(String.valueOf(SessionToken.Fields.PROPRIETARY_LOCATION), sessionToken.proprietaryLocation);
        map.put(String.valueOf(SessionToken.Fields.CURRENT_LOCATION), sessionToken.currentLocation);
        instance.hset(sessionToken.session, map);
        System.out.println("(SessionsDAO.setSessionToken) Session saved to local storage: " + sessionToken.getJson());
    }

    public static void deleteAllSessionTokens() {
        instance.deleteAll();
    }

    public static void deleteSessionToken(String sessionId) {
        instance.del(sessionId);
    }
}
