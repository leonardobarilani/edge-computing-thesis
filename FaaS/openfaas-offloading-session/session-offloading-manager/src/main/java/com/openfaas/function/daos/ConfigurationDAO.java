package com.openfaas.function.daos;

import java.util.Set;

public class ConfigurationDAO extends RedisDAO {

    private static class OffloadingPolicy {
        static String OFFLOADING = "offloading";
        static String ACCEPT = "accept";
        static String REJECT = "reject";
    }

    private static class SessionsLocksExpirationTime {
        static String SESSIONS_LOCKS_EXPIRATION_TIME = "sessions_locks_expiration_time";
    }

    private static class OffloadingThreshold {
        static String OFFLOAD_TOP_THRESHOLD = "offload_top_threshold";
        static String OFFLOAD_BOTTOM_THRESHOLD = "offload_bottom_threshold";
        static String ONLOAD_THRESHOLD = "onload_threshold";
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

    public static void setSessionsLocksExpirationTime(long time) {
        instance.set(SessionsLocksExpirationTime.SESSIONS_LOCKS_EXPIRATION_TIME, Long.toString(time));
    }

    public static long getSessionsLocksExpirationTime() {
        return Long.parseLong(instance.get(SessionsLocksExpirationTime.SESSIONS_LOCKS_EXPIRATION_TIME));
    }

    public static void setOffloadTopThreshold(long bytes) {
        instance.set(OffloadingThreshold.OFFLOAD_TOP_THRESHOLD, Long.toString(bytes));
    }

    public static void setOffloadBottomThreshold(long bytes) {
        instance.set(OffloadingThreshold.OFFLOAD_BOTTOM_THRESHOLD, Long.toString(bytes));
    }

    public static void setOnloadThreshold(long bytes) {
        instance.set(OffloadingThreshold.ONLOAD_THRESHOLD, Long.toString(bytes));
    }

    public static long getOffloadTopThreshold() {
        return Long.parseLong(instance.get(OffloadingThreshold.OFFLOAD_TOP_THRESHOLD));
    }

    public static long getOffloadBottomThreshold() {
        return Long.parseLong(instance.get(OffloadingThreshold.OFFLOAD_BOTTOM_THRESHOLD));
    }

    public static long getOnloadThreshold() {
        return Long.parseLong(instance.get(OffloadingThreshold.ONLOAD_THRESHOLD));
    }

    public static void addReceivingFunction(String function) {
        instance.sadd("receiving_functions", function);
    }

    public static Set<String> getAllReceivingFunctions() {
        return instance.smembers("receiving_functions");
    }
}
