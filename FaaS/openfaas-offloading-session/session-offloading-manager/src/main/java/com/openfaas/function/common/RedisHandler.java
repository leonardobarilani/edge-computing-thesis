package com.openfaas.function.common;

import com.openfaas.function.common.sessiondata.SessionData;
import com.openfaas.function.common.sessiondata.SessionRecord;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import io.lettuce.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisHandler {

    public static final String OFFLOAD = "0";
    public static final String SESSIONS = "1";
    public static final String SESSIONS_DATA = "2";
    public static final String RECEIVE_PROPAGATE_FUNCTIONS = "3";

    private String url;
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> syncCommands;

    /**
     * This constructor will use env variables for host, password and port.
     * @param table has to be 0 (OFFLOAD), 1 (SESSIONS), 2 (SESSIONS_DATA), 3 (RECEIVE_PROPAGATE_FUNCTIONS)
     */
    public RedisHandler(String table) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + table;

        System.out.println("(Redis Handler) (Constructor) Url: " + url);

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
        System.out.println("(Redis Handler) Redis get with key: " + key);
        if (key == null)
            return null;
        return syncCommands.get(key);
    }
    public void set(String key, String value){
        System.out.println("(Redis Handler) Redis set with key, value: " + key + ", " + value);
        syncCommands.set(key, value);
    }
    public String getRandom () {
        System.out.println("(Redis Handler) Redis randomkey");
        String randomKey = syncCommands.randomkey();
        if (randomKey == null)
            return null;
        System.out.println("(Redis Handler) Redis get with key: " + randomKey);
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
        System.out.println("(Redis Handler) Redis hlen");
        Long length = syncCommands.hlen(sessionId);
        SessionData data = new SessionData();
        data.session_data = new SessionRecord[Math.toIntExact(length)];

        System.out.println("(Redis Handler) Redis hgetall");
        var map = syncCommands.hgetall(sessionId);

        int i = 0;
        for(var entry : map.entrySet())
        {
            data.session_data[i] = new SessionRecord(entry.getKey(), entry.getValue());
            i++;
        }

        System.out.println("(Redis Handler) Redis returning session_data");
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

    public void addReceivingFunction (String function) {
        syncCommands.sadd("receiving_functions", function);

    }
    public Set<String> getAllReceivingFunctions () {
        return syncCommands.smembers("receiving_functions");
    }
}