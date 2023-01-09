package com.openfaas.function.daos;

import com.openfaas.function.model.SessionToken;

public class SessionsDAO extends RedisDAO {

    private static SessionsDAO instance = new SessionsDAO();

    private SessionsDAO() {
        super(Tables.SESSIONS);
    }

    public static SessionToken getSessionToken (String sessionId) {
        String json = null;

        if (sessionId != null)
            json = instance.get(sessionId);

        System.out.println("(SessionsDAO.getSessionToken) Session fetched from local storage: " + json);

        if (json != null)
            return new SessionToken().initJson(json);

        return null;
    }

    public static SessionToken getRandomSessionToken () {
        String json = instance.getRandom();

        System.out.println("(SessionsDAO.getRandomSessionToken) Session fetched from local storage: " + json);

        if (json != null)
            return new SessionToken().initJson(json);

        return null;
    }

    public static void setSessionToken (SessionToken sessionToken) {
        if (sessionToken != null)
            instance.set(sessionToken.session, sessionToken.getJson());
    }

    public static void deleteAllSessionTokens () {
        instance.deleteAll();
    }

    public static void deleteSessionToken (String sessionId) {
        instance.del(sessionId);
    }
}
