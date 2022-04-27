package com.openfaas.function;

import com.openfaas.model.IRequest;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import io.lettuce.core.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;

public class UserRedisHandler {

    public static final String SESSIONS_DATA = "2";

    private final String url;
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> syncCommands;
    private final String sessionId;

    /**
     * The default constructor will use env variables for host, password and port.
     * The table used is the sessions_data table (table 2)
     */
    public UserRedisHandler(IRequest req) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + SESSIONS_DATA;
        sessionId = req.getHeader("X-function-session");

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
        System.out.println("(UserRedisHandler) (sessionId: "+sessionId+") Redis get with key: " + key);
        return syncCommands.hget(sessionId, key);
    }

    public void set(String key, String value){
        System.out.println("(UserRedisHandler) (sessionId: "+sessionId+") Redis set with key, value: " + key + ", " + value);
        syncCommands.hset(sessionId, Map.ofEntries(entry(key, value)));
    }

    public void delete(String key) {
        syncCommands.hdel(sessionId, key);
    }
}