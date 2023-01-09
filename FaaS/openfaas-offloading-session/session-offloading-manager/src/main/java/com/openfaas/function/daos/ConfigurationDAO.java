package com.openfaas.function.daos;

public class ConfigurationDAO extends RedisDAO {

    private static ConfigurationDAO instance = new ConfigurationDAO();

    private ConfigurationDAO() {
        super(Tables.CONFIGURATION);
    }

    public static void acceptOffloading () {
        instance.set("offloading", "accept");
    }

    public static void rejectOffloading () {
        instance.set("offloading", "reject");
    }

    public static String getOffloading () {
        return instance.get("offloading");
    }
}
