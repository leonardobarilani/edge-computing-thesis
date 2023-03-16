package com.openfaas.function.api;

import com.google.gson.Gson;
import com.openfaas.function.daos.RedisProxyDAO;
import com.openfaas.function.daos.SessionsDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.openfaas.function.daos.RedisDAO.SESSIONS_DATA;
import static java.util.Map.entry;

public class EdgeDB extends RedisProxyDAO {

    private static String sessionId;
    private static EdgeDB instance = new EdgeDB();

    private EdgeDB() {
        super(SESSIONS_DATA);
    }

    public static String get(String key) {
        System.out.println("(EdgeDB.get) (sessionId: " + sessionId + ") get with key: " + key);
        return instance.hget(sessionId, key);
    }

    public static void set(String key, String value) {
        System.out.println("(EdgeDB.set) (sessionId: " + sessionId + ") set with key, value: " + key + ", " + value);
        instance.hset(sessionId, Map.ofEntries(entry(key, value)));
    }

    public static List<String> getList(String key) {
        System.out.println("(EdgeDB.getList) (sessionId: " + sessionId + ") Redis get with key: " + key);
        String rawList = instance.hget(sessionId, key);
        if (rawList == null) {
            System.out.println("(EdgeDB.getList) (sessionId: " + sessionId + ") null value from Redis get with key: <" + key + ">. Returning new empty list");
            return new ArrayList<>();
        }
        System.out.println("(EdgeDB.getList) (sessionId: " + sessionId + ") Parsing with Gson");
        return new Gson().fromJson(rawList, HList.class).list;
    }

    public static void setList(String key, List<String> list) {
        HList hlist = new HList();
        hlist.list = list;
        System.out.println("(EdgeDB.setList) (sessionId: " + sessionId + ") Parsing with Gson.toJson");
        String newJsonList = new Gson().toJson(hlist);
        System.out.println("(EdgeDB.setList) (sessionId: " + sessionId + ") Redis set with key, value: " + key + ", " + newJsonList);
        instance.hset(sessionId, Map.ofEntries(entry(key, newJsonList)));
    }

    public static void delete(String key) {
        System.out.println("(EdgeDB.delete) (sessionId: " + sessionId + ") Deleting with key: " + key);
        instance.hdel(sessionId, key);
    }

    static void setCurrentSession (String sessionId) {
        instance = new EdgeDB();
        System.out.println("(EdgeDB.setCurrentSession) Current session id: " + sessionId);
        instance.sessionId = sessionId;
    }

    static Map<String, String> getCache() {
        return instance.getLocalCache();
    }

    private static class HList {
        List<String> list = new ArrayList<>();
    }

    public static String getCurrentVirtualLocation() {
        return SessionsDAO.getSessionToken(instance.sessionId).proprietaryLocation;
    }
}
