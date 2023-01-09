package com.openfaas.function.daos;

import java.util.Set;

public class ReceivePropagateFunctionsDAO extends RedisDAO {

    private static ReceivePropagateFunctionsDAO instance = new ReceivePropagateFunctionsDAO();

    private ReceivePropagateFunctionsDAO() {
        super(Tables.RECEIVE_PROPAGATE_FUNCTIONS);
    }

    public static void addReceivingFunction (String function) {
        instance.sadd("receiving_functions", function);
    }

    public static Set<String> getAllReceivingFunctions () {
        return instance.smembers("receiving_functions");
    }
}
