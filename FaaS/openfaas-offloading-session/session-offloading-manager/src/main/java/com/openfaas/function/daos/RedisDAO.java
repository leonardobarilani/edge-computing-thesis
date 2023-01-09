package com.openfaas.function.daos;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.Map;
import java.util.Set;

abstract class RedisDAO {

    private String url;
    private RedisClient redisClient;
    private RedisCommands<String, String> syncCommands;

    /**
     * This constructor will use env variables for host, password and port.
     * @param table has to be 0 (OFFLOAD), 1 (SESSIONS), 2 (SESSIONS_DATA), 3 (RECEIVE_PROPAGATE_FUNCTIONS)
     */
    RedisDAO(String table) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + table;

        System.out.println("(RedisDAO) (Constructor) Url: " + url);

        redisClient = RedisClient.create(url);
    }

    void close() {
        redisClient.shutdown();
    }

    String get(String key){
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        String returnValue = null;
        if (key != null) {
            System.out.println("(RedisDAO.get) Redis get with key: " + key);
            returnValue = syncCommands.get(key);
        }

        connection.close();
        return returnValue;
    }

    void set(String key, String value){
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        if (key != null && value != null) {
            System.out.println("(RedisDAO.set) Redis set with key, value: " + key + ", " + value);
            syncCommands.set(key, value);
        }

        connection.close();
    }

    void sadd (String key, String value) {
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        if (key != null && value != null) {
            System.out.println("(RedisDAO.sadd) Redis sadd with key, value: " + key + ", " + value);
            syncCommands.sadd(key, value);
        }

        connection.close();
    }

    Set<String> smembers (String key) {
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        Set<String> returnValue = null;
        if (key != null) {
            System.out.println("(RedisDAO.smembers) Redis smembers with key: " + key);
            returnValue = syncCommands.smembers(key);
        }

        connection.close();
        return returnValue;
    }

    String getRandom () {
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        System.out.println("(RedisDAO.getRandom) Redis randomkey");
        String randomKey = syncCommands.randomkey();

        String returnValue = null;
        if (randomKey != null) {
            System.out.println("(RedisDAO.getRandom) Redis get with key: " + randomKey);
            returnValue = syncCommands.get(randomKey);
        }

        connection.close();
        return returnValue;
    }

    void deleteAll () {
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        System.out.println("(RedisDAO.getRandom) Redis randomkey");
        String key = syncCommands.randomkey();
        while (key != null) {
            System.out.println("(RedisDAO.getRandom) Redis del with key: " + key);
            syncCommands.del(key);
            System.out.println("(RedisDAO.getRandom) Redis randomkey");
            key = syncCommands.randomkey();
        }

        connection.close();
    }

    void hset (String key, Map<String, String> map){
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        if (key != null && map != null) {
            System.out.println("(RedisDAO.hset) Redis hset with key, map: " + key + ", " + map);
            syncCommands.hset(key, map);
        }

        connection.close();
    }

    Map<String, String> hgetall (String key){
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        Map<String, String> returnValue = null;
        if (key != null) {
            System.out.println("(RedisDAO.hgetall) Redis hgetall with key: " + key);
            returnValue = syncCommands.hgetall(key);
        }

        connection.close();
        return returnValue;
    }

    void del (String key){
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        if (key != null) {
            System.out.println("(RedisDAO.del) Redis del with key, value: " + key);
            syncCommands.del(key);
        }

        connection.close();
    }

    Long hlen (String key){
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        syncCommands = connection.sync();

        Long returnValue = null;
        if (key != null) {
            System.out.println("(RedisDAO.hlen) Redis hlen with key: " + key);
            returnValue = syncCommands.hlen(key);
        }

        connection.close();
        return returnValue;
    }
}
