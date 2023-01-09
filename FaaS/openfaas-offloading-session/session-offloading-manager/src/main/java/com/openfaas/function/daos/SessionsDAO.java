package com.openfaas.function.daos;

import com.openfaas.function.model.SessionToken;

public class SessionsDAO extends RedisDAO {

    private static SessionsDAO instance;

    private SessionsDAO() {
        super(Tables.SESSIONS);
    }

    public static SessionsDAO getInstance() {
        if (instance == null) {
            instance = new SessionsDAO();
        }
        return instance;
    }

    public static SessionToken getSessionToken (String sessionId) {
        String json = null;

        if (sessionId != null)
            json = instance.get(sessionId);

        System.out.println("session Token: "+json);

        if (json != null)
            return new SessionToken().initJson(json);

        return null;
    }

    public static SessionToken getRandomSessionToken () {
        String json = instance.getRandom();

        System.out.println("session Token: "+json);

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
