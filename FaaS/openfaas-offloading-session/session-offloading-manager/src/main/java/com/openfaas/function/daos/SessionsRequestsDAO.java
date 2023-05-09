package com.openfaas.function.daos;

import java.util.Set;

public class SessionsRequestsDAO extends RedisDAO {

    private static final SessionsRequestsDAO instance = new SessionsRequestsDAO();

    private SessionsRequestsDAO() {
        super(SESSIONS_REQUESTS);
    }

    public static void addSessionRequests(String sessionId, String... requestId) {
        instance.sadd(sessionId, requestId);
    }

    public static Set<String> getSessionRequests(String sessionId) {
        return instance.smembers(sessionId);
    }

    public static boolean existsSessionRequest(String sessionId, String requestId) {
        var members = instance.smembers(sessionId);
        return members.contains(requestId);
    }

    public static void deleteSessionRequest(String sessionsId) {
        instance.del(sessionsId);
    }
}
