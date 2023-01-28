package com.openfaas.function.commands.wrappers;

public class WrapperOnloadSession extends HTTPWrapper {
    public WrapperOnloadSession() {
        super();
    }

    public WrapperOnloadSession gateway (String gateway) {
        this.setGateway(gateway);
        return this;
    }

    @Override
    public Response call() {
        setRemoteFunction("/function/session-offloading-manager?command=onload-session");
        setHeader("X-onload-location", System.getenv("LOCATION_ID"));
        try {
            get ();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Response(getBody(), getStatusCode());
    }
}
