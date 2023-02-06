package com.openfaas.function.daos;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class JedisDAO extends StatefulDAO {

    private final String url;
    private final Jedis jedis;

    /**
     * This constructor will use env variables for host, password and port.
     *
     * @param table has to be 0 (OFFLOAD), 1 (SESSIONS), 2 (SESSIONS_DATA), 3 (RECEIVE_PROPAGATE_FUNCTIONS)
     */
    JedisDAO(String table) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + table;

        System.out.println("(JedisDAO) (Constructor) Url: " + url);
        /*try {
            jedis = new Jedis(new URI(url));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }*/
        jedis = new Jedis(host, Integer.parseInt(port));
        jedis.select(Integer.parseInt(table));
    }

    String get(String key) {
        String returnValue = null;
        if (key != null) {
            System.out.println("(JedisDAO.get) Redis get with key: " + key);
            returnValue = jedis.get(key);
        }
        return returnValue;
    }

    void set(String key, String value) {
        if (key != null && value != null) {
            System.out.println("(JedisDAO.set) Redis set with key, value: " + key + ", " + value);
            jedis.set(key, value);
        }
    }

    List<String> getAllKeys() {
        List<String> returnValue;
        System.out.println("(JedisDAO.getAllKeys) Redis keys");
        returnValue = new ArrayList<>(jedis.keys("*"));
        return returnValue;
    }

    void sadd(String key, String value) {
        if (key != null && value != null) {
            System.out.println("(JedisDAO.sadd) Redis sadd with key, value: " + key + ", " + value);
            jedis.sadd(key, value);
        }
    }

    Set<String> smembers(String key) {
        Set<String> returnValue = null;
        if (key != null) {
            System.out.println("(JedisDAO.smembers) Redis smembers with key: " + key);
            returnValue = jedis.smembers(key);
        }
        return returnValue;
    }

    String getRandom() {
        System.out.println("(JedisDAO.getRandom) Redis randomKey");
        String randomKey = jedis.randomKey();

        String returnValue = null;
        if (randomKey != null) {
            System.out.println("(JedisDAO.getRandom) Redis get with key: " + randomKey);
            returnValue = jedis.get(randomKey);
        }
        return returnValue;
    }

    void deleteAll() {
        System.out.println("(JedisDAO.getRandom) Redis randomKey");
        String key = jedis.randomKey();
        while (key != null) {
            System.out.println("(JedisDAO.getRandom) Redis del with key: " + key);
            jedis.del(key);
            System.out.println("(JedisDAO.getRandom) Redis randomKey");
            key = jedis.randomKey();
        }
    }

    void hset(String key, Map<String, String> map) {
        if (key != null && map != null) {
            System.out.println("(JedisDAO.hset) Redis hset with key, map: " + key + ", " + map);

            for (var entry : map.entrySet()) {
                jedis.hset(key, entry.getKey(), entry.getValue());
            }
        }
    }

    String hget(String key, String field) {
        String returnValue = null;
        if (key != null && field != null) {
            System.out.println("(JedisDAO.hget) Redis hget with key, field: " + key + ", " + field);
            returnValue = jedis.hget(key, field);
        }
        return returnValue;
    }

    Map<String, String> hgetall(String key) {
        Map<String, String> returnValue = null;
        if (key != null) {
            System.out.println("(JedisDAO.hgetall) Redis hgetall with key: " + key);
            returnValue = jedis.hgetAll(key);
        }
        return returnValue;
    }

    void del(String key) {
        if (key != null) {
            System.out.println("(JedisDAO.del) Redis del with key, value: " + key);
            jedis.del(key);
        }
    }

    Long hlen(String key) {
        Long returnValue = null;
        if (key != null) {
            System.out.println("(JedisDAO.hlen) Redis hlen with key: " + key);
            returnValue = jedis.hlen(key);
        }
        return returnValue;
    }

    boolean eval(String script, String accessedKey, String scriptArgument) {
        System.out.println("(JedisDAO.eval) Redis eval");
        return (Boolean) jedis.eval(script, List.of(accessedKey), List.of(scriptArgument));
    }
}
