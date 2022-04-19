package com.openfaas.function.common;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import io.lettuce.core.*;

import java.util.concurrent.TimeUnit;

public class RedisHandler {

    private String url;
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> syncCommands;

    public RedisHandler() {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/0";

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

    public String getRandomSession () {
        System.out.println("Redis randomkey");
        String randomKey = syncCommands.randomkey();
        System.out.println("Redis get with key: " + randomKey);
        return syncCommands.get(randomKey);
    }
}