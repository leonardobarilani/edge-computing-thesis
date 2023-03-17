package com.openfaas.function.daos;

import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class RedisDAO extends StatefulDAO {

    public static final String CONFIGURATION = "0";
    public static final String SESSIONS = "1";
    public static final String SESSIONS_DATA = "2";
    public static final String SESSIONS_LOCKS = "3";
    // IDs to identify each request and guarantee client consistency
    public static final String SESSIONS_REQUESTS = "4";

    private final String url;
    private StatefulRedisConnection<String, String> connection;
    private RedisClient redisClient;

    /**
     * This constructor will use env variables for host, password and port.
     *
     * @param table has to be 0 (OFFLOAD), 1 (SESSIONS), 2 (SESSIONS_DATA), 3 (RECEIVE_PROPAGATE_FUNCTIONS)
     */
    protected RedisDAO(String table) {
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

    void sadd(String key, String ... value) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null && value != null) {
            System.out.println("(RedisDAO.sadd) Redis sadd with key, value: " + key + ", " + Arrays.toString(value));
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
            System.out.println("(RedisDAO.smembers) Members: " + returnValue);
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

    protected void hset(String key, Map<String, String> map) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null && map != null && !map.isEmpty()) {
            System.out.println("(RedisDAO.hset) Redis hset with key, map: " + key + ", " + map);
            syncCommands.hset(key, map);
        } else {
            System.out.println("(RedisDAO.hset) Cannot execute redis hset with key, map: " + key + ", " + map);
        }

        closeConnection();
    }

    protected String hget(String key, String field) {
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

    protected Boolean hexists(String key, String field) {
        RedisCommands<String, String> syncCommands = openConnection();

        Boolean returnValue = null;
        if (key != null && field != null) {
            System.out.println("(RedisDAO.hexists) Redis hexists with key: " + key);
            returnValue = syncCommands.hexists(key, field);
        }

        closeConnection();
        return returnValue;
    }

    protected Long exists(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        Long returnValue = null;
        if (key != null) {
            System.out.println("(RedisDAO.exists) Redis exists with key: " + key);
            returnValue = syncCommands.exists(key);
        }

        closeConnection();
        return returnValue;
    }

    protected Long hdel(String key, String field) {
        RedisCommands<String, String> syncCommands = openConnection();

        Long returnValue = null;
        if (key != null && field != null) {
            System.out.println("(RedisDAO.hdel) Redis hdel with key: " + key);
            returnValue = syncCommands.hdel(key, field);
        }

        closeConnection();
        return returnValue;
    }

    boolean eval(String script, String[] accessedKey, String ... scriptArgument) {
        RedisCommands<String, String> syncCommands = openConnection();

        System.out.println("(RedisDAO.eval) Redis eval:");
        System.out.println("(RedisDAO.eval) Script: " + script);
        System.out.println("(RedisDAO.eval) AccessedKeys: " + Arrays.toString(accessedKey));
        System.out.println("(RedisDAO.eval) ScriptArguments: " + Arrays.toString(scriptArgument));
        boolean returnObject = syncCommands.eval(script, ScriptOutputType.BOOLEAN, accessedKey, scriptArgument);

        closeConnection();
        return returnObject;
    }

    void setIfNotExists(String key, String value, long timeout) {
        RedisCommands<String, String> syncCommands = openConnection();

        SetArgs args = new SetArgs();
        args.nx();
        args.ex(timeout);
        System.out.println("(RedisDAO.setIfNotExists) Redis set: " + key + ", " + value + ", " + timeout);
        syncCommands.set(key, value, args);

        closeConnection();
    }

    long memoryUsage(String sessionId) {
        RedisCommands<String, String> syncCommands = openConnection();

        long returnValue = syncCommands.memoryUsage(sessionId);

        closeConnection();

        return returnValue;
    }
}
