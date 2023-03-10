package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.function.commands.wrappers.HTTPWrapper;
import com.openfaas.function.commands.wrappers.Response;
import com.openfaas.function.utils.EdgeInfrastructureUtils;

import java.util.UUID;

public class CounterWrapper extends HTTPWrapper {

    private String product;

    CounterWrapper product(String product) {
        this.product = product;
        return this;
    }

    @Override
    public Response call() {
        setGateway(EdgeInfrastructureUtils.getParentHost(
                EdgeDB.getCurrentVirtualLocation()
        ));
        setHeader("X-session", "counter-" + EdgeInfrastructureUtils.getParentLocationId(
                EdgeDB.getCurrentVirtualLocation()
        ));
        setHeader("X-session-request-id", UUID.randomUUID().toString());
        setRemoteFunction("/function/products-counter?product=" + product);

        try {
            get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Response(getBody(), getStatusCode());
    }
}
