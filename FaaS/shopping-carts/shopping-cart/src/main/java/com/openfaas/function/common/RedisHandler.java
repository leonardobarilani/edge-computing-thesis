package com.openfaas.function.common;

import com.openfaas.function.common.datastructures.session.SessionData;
import com.openfaas.function.common.datastructures.session.SessionRecord;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RedisHandler {

    public static final String OFFLOAD = "0";
    public static final String SESSIONS = "1";
    public static final String SESSIONS_DATA = "2";
    public static final String SESSIONS_DATA_WITH_DOMAIN = "3";

    private String url;
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> syncCommands;

    /**
     * The default constructor will use env variables for host, password and port.
     * The table used is the sessions table (table 1)
     */
    public RedisHandler() {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + SESSIONS;

        redisClient = RedisClient.create(url);
        redisClient.setDefaultTimeout(20, TimeUnit.SECONDS);
        connection = redisClient.connect();
        syncCommands = connection.sync();
    }

    /**
     * This constructor will use env variables for host, password and port.
     * @param table has to be 0 (OFFLOAD), 1 (SESSIONS), 2 (SESSIONS_DATA)
     */
    public RedisHandler(String table) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + table;

        redisClient = RedisClient.create(url);
        redisClient.setDefaultTimeout(20, TimeUnit.SECONDS);
        connection = redisClient.connect();
        syncCommands = connection.sync();
    }

    public void close() {
        connection.close();
        redisClient.shutdown();
    }

    public String get(String key){
        System.out.println("Redis exists with key: " + key);
        if (syncCommands.exists(key) == 0)
            return null;
        System.out.println("Redis get with key: " + key);
        return syncCommands.get(key);
    }

    public void set(String key, String value){
        System.out.println("Redis set with key, value: " + key + ", " + value);
        syncCommands.set(key, value);
    }

    public String getRandom () {
        System.out.println("Redis randomkey");
        String randomKey = syncCommands.randomkey();
        System.out.println("Redis get with key: " + randomKey);
        return syncCommands.get(randomKey);
    }

    public void deleteAll() {
        String key = syncCommands.randomkey();
        while (key != null) {
            syncCommands.del(key);
            key = syncCommands.randomkey();
        }
    }

    public void delete(String key) {
        syncCommands.del(key);
    }

    /**
     * Supposed to be used only with table SESSIONS_DATA
     * @param sessionId
     * @return
     */
    public SessionData getSessionData (String sessionId) {
        System.out.println("Redis hlen");
        Long length = syncCommands.hlen(sessionId);
        SessionData data = new SessionData();
        data.session_data = new SessionRecord[Math.toIntExact(length)];

        System.out.println("Redis hgetall");
        var map = syncCommands.hgetall(sessionId);

        int i = 0;
        for(var entry : map.entrySet())
        {
            data.session_data[i] = new SessionRecord(entry.getKey(), entry.getValue());
            i++;
        }

        System.out.println("Redis returning session_data");
        return data;
    }

    /**
     * Supposed to be used only with table SESSIONS_DATA
     * @param sessionId
     */
    public void deleteSession (String sessionId) {
        syncCommands.del(sessionId);
    }

    /**
     * Supposed to be used only with table SESSIONS_DATA
     * @param sessionId
     * @param data
     */
    public void setSessionData (String sessionId, SessionData data) {
        Map<String, String> mapData = new HashMap<>();

        for(var entry : data.session_data)
            mapData.put(entry.key, entry.data);

        syncCommands.hset(sessionId, mapData);
    }
}