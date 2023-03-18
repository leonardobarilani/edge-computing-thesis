package com.openfaas.function.daos;

import com.openfaas.function.utils.Logger;
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

        Logger.log("(RedisDAO) (Constructor) Url: " + url);
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
            Logger.log("(RedisDAO.get) Redis get with key: " + key);
            returnValue = syncCommands.get(key);
        }

        closeConnection();
        return returnValue;
    }

    void set(String key, String value) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null && value != null) {
            Logger.log("(RedisDAO.set) Redis set with key, value: " + key + ", " + value);
            syncCommands.set(key, value);
        }

        closeConnection();
    }

    List<String> getAllKeys() {
        RedisCommands<String, String> syncCommands = openConnection();

        List<String> returnValue;
        Logger.log("(RedisDAO.getAllKeys) Redis keys");
        returnValue = syncCommands.keys("*");

        closeConnection();
        return returnValue;
    }

    void sadd(String key, String ... value) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null && value != null) {
            Logger.log("(RedisDAO.sadd) Redis sadd with key, value: " + key + ", " + Arrays.toString(value));
            syncCommands.sadd(key, value);
        }

        closeConnection();
    }

    Set<String> smembers(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        Set<String> returnValue = null;
        if (key != null) {
            Logger.log("(RedisDAO.smembers) Redis smembers with key: " + key);
            returnValue = syncCommands.smembers(key);
            Logger.log("(RedisDAO.smembers) Members: " + returnValue);
        }

        closeConnection();
        return returnValue;
    }

    String getRandom() {
        RedisCommands<String, String> syncCommands = openConnection();

        Logger.log("(RedisDAO.getRandom) Redis randomkey");
        String randomKey = syncCommands.randomkey();

        String returnValue = null;
        if (randomKey != null) {
            Logger.log("(RedisDAO.getRandom) Redis get with key: " + randomKey);
            returnValue = syncCommands.get(randomKey);
        }

        closeConnection();
        return returnValue;
    }

    void deleteAll() {
        RedisCommands<String, String> syncCommands = openConnection();

        Logger.log("(RedisDAO.getRandom) Redis randomkey");
        String key = syncCommands.randomkey();
        while (key != null) {
            Logger.log("(RedisDAO.getRandom) Redis del with key: " + key);
            syncCommands.del(key);
            Logger.log("(RedisDAO.getRandom) Redis randomkey");
            key = syncCommands.randomkey();
        }

        closeConnection();
    }

    protected void hset(String key, Map<String, String> map) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null && map != null && !map.isEmpty()) {
            Logger.log("(RedisDAO.hset) Redis hset with key, map: " + key + ", " + map);
            syncCommands.hset(key, map);
        } else {
            Logger.log("(RedisDAO.hset) Cannot execute redis hset with key, map: " + key + ", " + map);
        }

        closeConnection();
    }

    protected String hget(String key, String field) {
        RedisCommands<String, String> syncCommands = openConnection();

        String returnValue = null;
        if (key != null && field != null) {
            Logger.log("(RedisDAO.hget) Redis hget with key, field: " + key + ", " + field);
            returnValue = syncCommands.hget(key, field);
        }

        closeConnection();
        return returnValue;
    }

    Map<String, String> hgetall(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        Map<String, String> returnValue = null;
        if (key != null) {
            Logger.log("(RedisDAO.hgetall) Redis hgetall with key: " + key);
            returnValue = syncCommands.hgetall(key);
        }

        closeConnection();
        return returnValue;
    }

    void del(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        if (key != null) {
            Logger.log("(RedisDAO.del) Redis del with key, value: " + key);
            syncCommands.del(key);
        }

        closeConnection();
    }

    Long hlen(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        Long returnValue = null;
        if (key != null) {
            Logger.log("(RedisDAO.hlen) Redis hlen with key: " + key);
            returnValue = syncCommands.hlen(key);
        }

        closeConnection();
        return returnValue;
    }

    protected Boolean hexists(String key, String field) {
        RedisCommands<String, String> syncCommands = openConnection();

        Boolean returnValue = null;
        if (key != null && field != null) {
            Logger.log("(RedisDAO.hexists) Redis hexists with key: " + key);
            returnValue = syncCommands.hexists(key, field);
        }

        closeConnection();
        return returnValue;
    }

    protected Long exists(String key) {
        RedisCommands<String, String> syncCommands = openConnection();

        Long returnValue = null;
        if (key != null) {
            Logger.log("(RedisDAO.exists) Redis exists with key: " + key);
            returnValue = syncCommands.exists(key);
        }

        closeConnection();
        return returnValue;
    }

    protected Long hdel(String key, String field) {
        RedisCommands<String, String> syncCommands = openConnection();

        Long returnValue = null;
        if (key != null && field != null) {
            Logger.log("(RedisDAO.hdel) Redis hdel with key: " + key);
            returnValue = syncCommands.hdel(key, field);
        }

        closeConnection();
        return returnValue;
    }

    boolean eval(String script, String[] accessedKey, String ... scriptArgument) {
        RedisCommands<String, String> syncCommands = openConnection();

        Logger.log("(RedisDAO.eval) Redis eval:");
        Logger.log("(RedisDAO.eval) Script: " + script);
        Logger.log("(RedisDAO.eval) AccessedKeys: " + Arrays.toString(accessedKey));
        Logger.log("(RedisDAO.eval) ScriptArguments: " + Arrays.toString(scriptArgument));
        boolean returnObject = syncCommands.eval(script, ScriptOutputType.BOOLEAN, accessedKey, scriptArgument);

        closeConnection();
        return returnObject;
    }

    void setIfNotExists(String key, String value, long timeout) {
        RedisCommands<String, String> syncCommands = openConnection();

        SetArgs args = new SetArgs();
        args.nx();
        args.ex(timeout);
        Logger.log("(RedisDAO.setIfNotExists) Redis set: " + key + ", " + value + ", " + timeout);
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
