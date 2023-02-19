package com.openfaas.function.daos;

import java.util.Set;

public class ConfigurationDAO extends RedisDAO {

    private static class OffloadingPolicy {
        static String OFFLOADING = "offloading";
        static String ACCEPT = "accept";
        static String REJECT = "reject";
    }

    private static class ExpirationTime {
        static String SESSIONS_EXPIRATION_TIME = "sessions_expiration_time";
    }

    private static final ConfigurationDAO instance = new ConfigurationDAO();

    private ConfigurationDAO() {
        super(CONFIGURATION);
    }

    public static void acceptOffloading() {
        instance.set(OffloadingPolicy.OFFLOADING, OffloadingPolicy.ACCEPT);
    }

    public static void rejectOffloading() {
        instance.set(OffloadingPolicy.OFFLOADING, OffloadingPolicy.REJECT);
    }

    public static String getOffloading() {
        return instance.get(OffloadingPolicy.OFFLOADING);
    }

    public static void setSessionsExpirationTime(long time) {
        instance.set(ExpirationTime.SESSIONS_EXPIRATION_TIME, Long.toString(time));
    }

    public static long getSessionsExpirationTime() {
        return Long.parseLong(instance.get(ExpirationTime.SESSIONS_EXPIRATION_TIME));
    }

    public static void addReceivingFunction(String function) {
        instance.sadd("receiving_functions", function);
    }

    public static Set<String> getAllReceivingFunctions() {
        return instance.smembers("receiving_functions");
    }
}
