package com.openfaas.function.common;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import redis.clients.jedis.Jedis;


import io.lettuce.core.*;


public class JedisHandler {

    private Jedis jedis;

    /**
     * The default constructor initializes the handler through
     * environment variables REDIS_HOST, REDIS_PORT, REDIS_PASSWORD
     */
    public JedisHandler () {
        //jedis = new Jedis(System.getenv("REDIS_HOST"), Integer.getInteger(System.getenv("REDIS_PORT")));
        //jedis.auth(System.getenv("REDIS_PASSWORD"));
    }

    public JedisHandler(String host, int port, String password) {
        //jedis = new Jedis(host, port);
        //jedis.auth(password);
    }

    public String get(String key){

        //System.out.println("lettuce create");
        RedisClient redisClient = RedisClient.create(
                "redis://"+System.getenv("REDIS_PASSWORD")+"@"+
                        System.getenv("REDIS_HOST")+":"+System.getenv("REDIS_PORT")+"/0");

        //System.out.println("lettuce connect");
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        //System.out.println("lettuce sync");
        RedisCommands<String, String> syncCommands = connection.sync();

        System.out.println("lettuce get");
        String value = syncCommands.get(key);

        //System.out.println("lettuce close");
        connection.close();
        //System.out.println("lettuce shutdown");
        redisClient.shutdown();
        return value;
        //return jedis.get(key);
    }

    public void set(String key, String value){
        //System.out.println("lettuce create");
        RedisClient redisClient = RedisClient.create(
                "redis://"+System.getenv("REDIS_PASSWORD")+"@"+
                        System.getenv("REDIS_HOST")+":"+System.getenv("REDIS_PORT")+"/0");

        //System.out.println("lettuce connect");
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        //System.out.println("lettuce sync");
        RedisCommands<String, String> syncCommands = connection.sync();

        System.out.println("lettuce set");
        syncCommands.set(key, value);

        //System.out.println("lettuce close");
        connection.close();
        //System.out.println("lettuce shutdown");
        redisClient.shutdown();
        //jedis.set(key, value);
    }

    public String getRandomSession () {
        //System.out.println("lettuce create");
        RedisClient redisClient = RedisClient.create(
                "redis://"+System.getenv("REDIS_PASSWORD")+"@"+
                        System.getenv("REDIS_HOST")+":"+System.getenv("REDIS_PORT")+"/0");

        //System.out.println("lettuce connect");
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        //System.out.println("lettuce sync");
        RedisCommands<String, String> syncCommands = connection.sync();

        System.out.println("lettuce randomKey");
        String randomKey = syncCommands.randomkey();
        System.out.println("lettuce get");
        String value = syncCommands.get(randomKey);

        //System.out.println("lettuce close");
        connection.close();
        //System.out.println("lettuce shutdown");
        redisClient.shutdown();
        return value;
        //return jedis.get(jedis.randomKey());
    }
}