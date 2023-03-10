package com.openfaas.function.daos;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class StatefulDAO {

    abstract String get(String key);

    abstract void set(String key, String value);

    abstract List<String> getAllKeys();

    abstract void sadd(String key, String ... value);

    abstract Set<String> smembers(String key);

    abstract void deleteAll();

    abstract void hset(String key, Map<String, String> map);

    abstract String hget(String key, String field);

    abstract Map<String, String> hgetall(String key);

    abstract void del(String key);

    abstract Long hlen(String key);

    abstract boolean eval(String script, String[] accessedKey, String ... scriptArgument);
}
