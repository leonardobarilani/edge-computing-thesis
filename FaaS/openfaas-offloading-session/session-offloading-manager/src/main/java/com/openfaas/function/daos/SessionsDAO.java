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
            sessionToken.status = SessionToken.Status.valueOf(instance.hget(sessionId,
                    String.valueOf(SessionToken.Fields.STATUS)));
            System.out.println("(SessionsDAO.getSessionToken) Session fetched from local storage: " + sessionToken.getJson());
        }
        return sessionToken;
    }

    public static SessionToken getRandomSessionToken() {
        throw new UnsupportedOperationException();
        /*
        String json = instance.getRandom();

        System.out.println("(SessionsDAO.getRandomSessionToken) Session fetched from local storage: " + json);

        if (json != null)
            return SessionToken.Builder.buildFromJSON(json);

        return null;*/
    }

    public static void setSessionToken(SessionToken sessionToken) {
        HashMap<String, String> map = new HashMap<>();
        map.put(String.valueOf(SessionToken.Fields.PROPRIETARY_LOCATION), sessionToken.proprietaryLocation);
        map.put(String.valueOf(SessionToken.Fields.CURRENT_LOCATION), sessionToken.currentLocation);
        map.put(String.valueOf(SessionToken.Fields.STATUS), String.valueOf(sessionToken.status));
        instance.hset(sessionToken.session, map);
        System.out.println("(SessionsDAO.setSessionToken) Session saved to local storage: " + sessionToken.getJson());
    }

    /**
     * Returns true if the lock has been successfully acquired. False otherwise
     * @param sessionId
     * @return
     */
    public static boolean lockSession(String sessionId) {
        String script =
                "if redis.call('hget', ARGV[1], '" + SessionToken.Fields.STATUS + "') == '" + SessionToken.Status.UNLOCKED + "' then " +
                        "redis.call('hset', ARGV[1], '" + SessionToken.Fields.STATUS + "', '" + SessionToken.Status.LOCKED + "') ; " +
                        "return true " +
                        "else " +
                        "return false " +
                        "end";
        boolean returnValue = false;
        if (sessionId != null) {
            returnValue = instance.eval(script, sessionId, sessionId);
            if (returnValue) {
                System.out.println("(SessionsDAO.lockSession) Acquired lock on session <" + sessionId + ">");
            } else {
                System.out.println("(SessionsDAO.lockSession) Was not able to acquire lock on session <" + sessionId + ">");
            }
        } else {
            System.out.println("(SessionsDAO.lockSession) Session parameter equals to null");
        }
        return returnValue;
    }

    /**
     * Returns true if the lock has been successfully released. False otherwise
     * @param sessionId
     * @return
     */
    public static boolean unlockSession(String sessionId) {
        String script =
                "if redis.call('hget', ARGV[1], '" + SessionToken.Fields.STATUS + "') == '" + SessionToken.Status.LOCKED + "' then " +
                        "redis.call('hset', ARGV[1], '" + SessionToken.Fields.STATUS + "', '" + SessionToken.Status.UNLOCKED + "') ; " +
                        "return true " +
                        "else " +
                        "return false " +
                        "end";
        boolean returnValue = false;
        if (sessionId != null) {
            returnValue = instance.eval(script, sessionId, sessionId);
            if (returnValue) {
                System.out.println("(SessionsDAO.unlockSession) Released lock on session <" + sessionId + ">");
            } else {
                System.out.println("(SessionsDAO.unlockSession) Was not able to release lock on session <" + sessionId + ">");
            }
        } else {
            System.out.println("(SessionsDAO.unlockSession) Session parameter equals to null");
        }
        return returnValue;
    }

    public static void deleteAllSessionTokens() {
        instance.deleteAll();
    }

    public static void deleteSessionToken(String sessionId) {
        instance.del(sessionId);
    }
}
