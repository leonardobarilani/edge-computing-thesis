package com.openfaas.function.daos;

import java.util.Set;

public class ConfigurationDAO extends RedisDAO {

    private static class OffloadingPolicy {
        static String OFFLOADING = "offloading";
        static String ACCEPT = "accept";
        static String REJECT = "reject";
    }

    private static class CreatingSessionLock {
        static String CREATING_SESSION_LOCK = "creating_session_lock";
        static String LOCKED = "locked";
        static String UNLOCKED = "unlocked";
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

    public static boolean lockCreatingSession(){
        String script =
                "if redis.call('get', '" + CreatingSessionLock.CREATING_SESSION_LOCK + "') == '" + CreatingSessionLock.UNLOCKED + "' then " +
                        "redis.call('set', '" + CreatingSessionLock.CREATING_SESSION_LOCK + "', '" + CreatingSessionLock.LOCKED + "') ; " +
                        "return true " +
                "else " +
                        "return false " +
                "end";
        boolean returnValue = false;
        returnValue = instance.eval(script, CreatingSessionLock.CREATING_SESSION_LOCK, null);
        if (returnValue) {
            System.out.println("(ConfigurationDAO.lockCreatingSession) Acquired lock to create a session");
        } else {
            System.out.println("(ConfigurationDAO.lockCreatingSession) Was not able to acquire lock to create a session");
        }
        return returnValue;
    }

    public static boolean unlockCreatingSession(){
        String script =
                "if redis.call('get', '" + CreatingSessionLock.CREATING_SESSION_LOCK + "') == '" + CreatingSessionLock.LOCKED + "' then " +
                        "redis.call('set', '" + CreatingSessionLock.CREATING_SESSION_LOCK + "', '" + CreatingSessionLock.UNLOCKED + "') ; " +
                        "return true " +
                "else " +
                        "return false " +
                "end";
        boolean returnValue = false;
        returnValue = instance.eval(script, CreatingSessionLock.CREATING_SESSION_LOCK, null);
        if (returnValue) {
            System.out.println("(ConfigurationDAO.unlockCreatingSession) Released lock to create a session");
        } else {
            System.out.println("(ConfigurationDAO.unlockCreatingSession) Was not able to release");
        }
        return returnValue;
    }

    public static void addReceivingFunction(String function) {
        instance.sadd("receiving_functions", function);
    }

    public static Set<String> getAllReceivingFunctions() {
        return instance.smembers("receiving_functions");
    }
}
