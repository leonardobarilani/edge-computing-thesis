package com.openfaas.function.common;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import io.lettuce.core.*;

import java.util.concurrent.TimeUnit;

public class RedisHandler {

    public static final String OFFLOAD = "0";
    public static final String SESSIONS = "1";
    public static final String SESSIONS_KEYS = "2";
    public static final String SESSIONS_DATA = "3";

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
     * @param table has to be 0 (OFFLOAD) or 1 (SESSIONS)
     */
    public RedisHandler(String table) {
        assert table.equals(OFFLOAD) || table.equals(SESSIONS);
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
}