package com.openfaas.function.daos;

import com.openfaas.function.model.sessiondata.SessionData;
import com.openfaas.function.model.sessiondata.SessionRecord;

import java.util.HashMap;
import java.util.Map;

public class SessionsDataDAO extends RedisDAO {

    private static SessionsDataDAO instance;

    private SessionsDataDAO() {
        super(Tables.SESSIONS);
    }

    public static SessionsDataDAO getInstance() {
        if (instance == null) {
            instance = new SessionsDataDAO();
        }
        return instance;
    }

    public void setSessionData (String sessionId, SessionData data) {
        Map<String, String> mapData = new HashMap<>();

        for(var entry : data.session_data)
            mapData.put(entry.key, entry.data);

        instance.hset(sessionId, mapData);
    }

    public void deleteSessionData (String sessionId) {
        instance.del(sessionId);
    }

    public SessionData getSessionData (String sessionId) {
        Long length = instance.hlen(sessionId);
        SessionData data = new SessionData();
        data.session_data = new SessionRecord[Math.toIntExact(length)];

        var map = instance.hgetall(sessionId);

        int i = 0;
        for(var entry : map.entrySet())
        {
            data.session_data[i] = new SessionRecord(entry.getKey(), entry.getValue());
            i++;
        }

        System.out.println("(SessionDataDAO.getSessionData) Returning session_data");
        return data;
    }
}
