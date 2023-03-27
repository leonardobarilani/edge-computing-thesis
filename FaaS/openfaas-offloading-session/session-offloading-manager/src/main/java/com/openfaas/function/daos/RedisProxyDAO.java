package com.openfaas.function.daos;

import com.openfaas.function.utils.Logger;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.HashMap;
import java.util.Map;

public class RedisProxyDAO {

    private String url;
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private final HashMap<String, String> localCache;

    protected RedisProxyDAO(String table) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + table;

        Logger.log("(RedisProxyDAO) (Constructor) Url: " + url);

        localCache = new HashMap<>();
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

    protected Map<String, String> getLocalCache() {
        return localCache;
    }

    protected String hget(String sessionId, String key) {
        if (localCache.containsKey(key)) {
            return localCache.get(key);
        } else {
            var syncCommands = openConnection();
            String value = syncCommands.hget(sessionId, key);
            closeConnection();
            localCache.put(key, value);
            return value;
        }
    }

    protected void hset(String sessionId, Map<String, String> ofEntries) {
        localCache.putAll(ofEntries);
    }

    protected boolean hexists(String sessionId, String key) {
        if (localCache.containsKey(key))
            return true;
        var syncCommands = openConnection();
        boolean returnValue = syncCommands.hexists(sessionId, key);
        closeConnection();

        return returnValue;
    }

    protected void hdel(String sessionId, String key) {
        localCache.put(key, null);
    }
}
