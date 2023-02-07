package com.openfaas.function.daos;

import java.util.Set;

public class ConfigurationDAO extends RedisDAO {

    private static final ConfigurationDAO instance = new ConfigurationDAO();

    private ConfigurationDAO() {
        super(Tables.CONFIGURATION);
    }

    public static void acceptOffloading() {
        instance.set("offloading", "accept");
    }

    public static void rejectOffloading() {
        instance.set("offloading", "reject");
    }

    public static String getOffloading() {
        return instance.get("offloading");
    }

    public static void addReceivingFunction(String function) {
        instance.sadd("receiving_functions", function);
    }

    public static Set<String> getAllReceivingFunctions() {
        return instance.smembers("receiving_functions");
    }
}
