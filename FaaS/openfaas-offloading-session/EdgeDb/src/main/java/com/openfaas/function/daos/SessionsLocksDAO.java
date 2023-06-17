package com.openfaas.function.daos;

import com.openfaas.function.utils.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;

public class SessionsLocksDAO extends RedisDAO {

    private static final SessionsLocksDAO instance = new SessionsLocksDAO();
    private static String randomValue;

    private SessionsLocksDAO() {
        super(SESSIONS_LOCKS);
    }

    /**
     * Returns true if the lock has been successfully acquired. False otherwise
     *
     * @param sessionId
     * @return
     */
    public static boolean lockSession(String sessionId) {
        boolean returnValue = false;
        if (sessionId != null) {
            String randomValue = null;
            try {
                randomValue = String.valueOf(SecureRandom.getInstanceStrong().nextLong());
            } catch (NoSuchAlgorithmException e) {
                Logger.log("(SessionsLocksDAO.lockSession) Cannot create a random value");
                throw new RuntimeException(e);
            }
            instance.randomValue = randomValue;
            instance.setIfNotExists(sessionId, randomValue, ConfigurationDAO.getSessionsLocksExpirationTime());

            String checkRandomKey = instance.get(sessionId);
            returnValue = checkRandomKey.equals(instance.randomValue);
            if (returnValue) {
                Logger.log("(SessionsLocksDAO.lockSession) Acquired lock on session <" + sessionId + ">");
            } else {
                Logger.log("(SessionsLocksDAO.lockSession) Was not able to acquire lock on session <" + sessionId + ">");
            }
        } else {
            Logger.log("(SessionsLocksDAO.lockSession) Session parameter equals to null");
        }
        return returnValue;
    }

    /**
     * Returns true if the lock has been successfully released. False otherwise
     *
     * @param sessionId
     * @return
     */
    public static boolean unlockSessionAndUpdateData(String sessionId, Map<String, String> data, String requestId) {
        // ARGV[1] = sessionId
        // ARGV[2] = randomValue
        // ARGV[3] = requestId
        final String scriptBegin =
                "redis.call('select', '" + SESSIONS_LOCKS + "'); \n" +
                        "if redis.call('get', ARGV[1]) == ARGV[2] then \n" +
                        "redis.call('del', ARGV[1]) ; \n" +
                        "redis.call('select', '" + SESSIONS_REQUESTS + "') ; \n" +
                        "redis.call('sadd', ARGV[1], ARGV[3]) ; \n" +
                        "redis.call('select', '" + SESSIONS_DATA + "'); \n";
        final String scriptEnd =
                "return true \n" +
                        "else \n" +
                        "return false \n" +
                        "end";
        boolean returnValue = false;
        if (sessionId != null) {
            String mapAsHsets = data.keySet().stream()
                    .map(key -> "redis.call('hset', ARGV[1], '" + key + "', '" + data.get(key) + "') ; \n")
                    .reduce("", (subtotal, element) -> subtotal + element);
            String script = scriptBegin + mapAsHsets + scriptEnd;

            returnValue = instance.eval(script, new String[]{sessionId}, sessionId, randomValue, requestId);
            if (returnValue) {
                Logger.log("(SessionsLocksDAO.unlockSessionAndUpdateData) Released lock on session <" + sessionId + ">");
            } else {
                Logger.log("(SessionsLocksDAO.unlockSessionAndUpdateData) Was not able to release lock on session <" + sessionId + ">");
            }
        } else {
            Logger.log("(SessionsLocksDAO.unlockSessionAndUpdateData) Session parameter equals to null");
        }
        return returnValue;
    }

    /**
     * Returns true if the lock has been successfully released. False otherwise
     *
     * @param sessionId
     * @return
     */
    public static boolean unlockSession(String sessionId) {
        // ARGV[1] = sessionId
        // ARGV[2] = randomValue
        final String script =
                "redis.call('select', '" + SESSIONS_LOCKS + "')" +
                        "if redis.call('get', ARGV[1]) == ARGV[2] then " +
                        "redis.call('del', ARGV[1]) ; " +
                        "return true " +
                        "else " +
                        "return false " +
                        "end";
        boolean returnValue = false;
        if (sessionId != null) {
            returnValue = instance.eval(script, new String[]{sessionId}, sessionId, randomValue);
            if (returnValue) {
                Logger.log("(SessionsLocksDAO.unlockSession) Released lock on session <" + sessionId + ">");
            } else {
                Logger.log("(SessionsLocksDAO.unlockSession) Was not able to release lock on session <" + sessionId + ">");
            }
        } else {
            Logger.log("(SessionsLocksDAO.unlockSession) Session parameter equals to null");
        }
        return returnValue;
    }

    public static String getRandomValue() {
        return instance.randomValue;
    }

    public static void setRandomValue(String randomValue) {
        instance.randomValue = randomValue;
    }
}
