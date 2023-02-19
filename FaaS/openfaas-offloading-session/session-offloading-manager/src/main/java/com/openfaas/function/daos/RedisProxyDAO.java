package com.openfaas.function.daos;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.HashMap;
import java.util.Map;

public class RedisProxyDAO {

    private final RedisCommands<String, String> syncCommands;
    private final HashMap<String, String> localCache;

    protected RedisProxyDAO(String table) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        String url = "redis://" + password + "@" + host + ":" + port + "/" + table;

        System.out.println("(RedisProxyDAO) (Constructor) Url: " + url);

        localCache = new HashMap<>();

        RedisClient redisClient = RedisClient.create(url);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        syncCommands = connection.sync();
    }

    protected Map<String, String> getLocalCache() {
        return localCache;
    }

    protected String hget(String sessionId, String key) {
        if (localCache.containsKey(key)) {
            return localCache.get(key);
        } else {
            String value = syncCommands.hget(sessionId, key);
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
        return syncCommands.hexists(sessionId, key);
    }

    protected void hdel(String sessionId, String key) {
        localCache.put(key, null);
    }
}
