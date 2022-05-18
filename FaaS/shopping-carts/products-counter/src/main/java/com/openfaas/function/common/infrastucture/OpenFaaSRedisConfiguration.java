package com.openfaas.function.common.infrastucture;

public class OpenFaaSRedisConfiguration {

    public String openfaas_gateway;
    public String openfaas_password;
    public String redis_host;
    public int redis_port;
    public String redis_password;

    /* probably will be removed */
    public String location_id;
}
