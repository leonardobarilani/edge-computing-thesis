package com.openfaas.function.daos;

import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class RedisDAO extends StatefulDAO {

    private final String url;
    private StatefulRedisConnection<String, String> connection;
    private RedisClient redisClient;

    /**
     * This constructor will use env variables for host, password and port.
     *
     * @param table has to be 0 (OFFLOAD), 1 (SESSIONS), 2 (SESSIONS_DATA), 3 (RECEIVE_PROPAGATE_FUNCTIONS)
     */
    RedisDAO(String table) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + table;

        System.out.println("(RedisDAO) (Constructor) Url: " + url);
    }

    private RedisCommands<String, String> openConnection() {
        redisClient = RedisClient.create(url);
        connection = redisClient.connect();
        return connection.sync();
    }

    private void closeConnection() {
        connection.close();
        redisClient.shutdown();
    }

    String get(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        String returnValue = null;
        if (key != null) {
            System.out.println("(RedisDAO.get) Redis get with key: " + key);
            returnValue = syncCommands.get(key);
        }

        closeConnection();
        return returnValue;
    }

    void set(String key, String value) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null && value != null) {
            System.out.println("(RedisDAO.set) Redis set with key, value: " + key + ", " + value);
            syncCommands.set(key, value);
        }

        closeConnection();
    }

    List<String> getAllKeys() {
        RedisCommands<String, String> syncCommands = openConnection();

        List<String> returnValue;
        System.out.println("(RedisDAO.getAllKeys) Redis keys");
        returnValue = syncCommands.keys("*");

        closeConnection();
        return returnValue;
    }

    void sadd(String key, String value) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null && value != null) {
            System.out.println("(RedisDAO.sadd) Redis sadd with key, value: " + key + ", " + value);
            syncCommands.sadd(key, value);
        }

        closeConnection();
    }

    Set<String> smembers(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        Set<String> returnValue = null;
        if (key != null) {
            System.out.println("(RedisDAO.smembers) Redis smembers with key: " + key);
            returnValue = syncCommands.smembers(key);
        }

        closeConnection();
        return returnValue;
    }

    String getRandom() {
        RedisCommands<String, String> syncCommands = openConnection();

        System.out.println("(RedisDAO.getRandom) Redis randomkey");
        String randomKey = syncCommands.randomkey();

        String returnValue = null;
        if (randomKey != null) {
            System.out.println("(RedisDAO.getRandom) Redis get with key: " + randomKey);
            returnValue = syncCommands.get(randomKey);
        }

        closeConnection();
        return returnValue;
    }

    void deleteAll() {
        RedisCommands<String, String> syncCommands = openConnection();

        System.out.println("(RedisDAO.getRandom) Redis randomkey");
        String key = syncCommands.randomkey();
        while (key != null) {
            System.out.println("(RedisDAO.getRandom) Redis del with key: " + key);
            syncCommands.del(key);
            System.out.println("(RedisDAO.getRandom) Redis randomkey");
            key = syncCommands.randomkey();
        }

        closeConnection();
    }

    void hset(String key, Map<String, String> map) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null && map != null) {
            System.out.println("(RedisDAO.hset) Redis hset with key, map: " + key + ", " + map);
            syncCommands.hset(key, map);
        }

        closeConnection();
    }

    String hget(String key, String field) {
        RedisCommands<String, String> syncCommands = openConnection();

        String returnValue = null;
        if (key != null && field != null) {
            System.out.println("(RedisDAO.hget) Redis hget with key, field: " + key + ", " + field);
            returnValue = syncCommands.hget(key, field);
        }

        closeConnection();
        return returnValue;
    }

    Map<String, String> hgetall(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        Map<String, String> returnValue = null;
        if (key != null) {
            System.out.println("(RedisDAO.hgetall) Redis hgetall with key: " + key);
            returnValue = syncCommands.hgetall(key);
        }

        closeConnection();
        return returnValue;
    }

    void del(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null) {
            System.out.println("(RedisDAO.del) Redis del with key, value: " + key);
            syncCommands.del(key);
        }

        closeConnection();
    }

    Long hlen(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        Long returnValue = null;
        if (key != null) {
            System.out.println("(RedisDAO.hlen) Redis hlen with key: " + key);
            returnValue = syncCommands.hlen(key);
        }

        closeConnection();
        return returnValue;
    }

    boolean eval(String script, String accessedKey, String scriptArgument) {
        RedisCommands<String, String> syncCommands = openConnection();

        System.out.println("(RedisDAO.eval) Redis eval:");
        System.out.println("(RedisDAO.eval) Script: " + script);
        System.out.println("(RedisDAO.eval) AccessedKey: " + accessedKey);
        System.out.println("(RedisDAO.eval) ScriptArgument: " + scriptArgument);
        boolean returnObject = syncCommands.eval(script, ScriptOutputType.BOOLEAN, new String[]{ accessedKey }, scriptArgument);

        closeConnection();
        return returnObject;
    }
}
