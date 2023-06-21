package com.openfaas.function.commands.wrappers;

public class WrapperGarbageCollector extends HTTPWrapper {

    private String sessionToDelete;

    public WrapperGarbageCollector() {
        super();
    }

    public WrapperGarbageCollector gateway(String gateway) {
        this.setGateway(gateway);
        return this;
    }

    public WrapperGarbageCollector sessionToDelete(String session) {
        this.sessionToDelete = session;
        return this;
    }

    @Override
    public Response call() {
        setRemoteFunction("/function/session-offloading-manager?" +
                "command=garbage-collector&" +
                "deletePolicy=forced&" +
                "sessionId=" + sessionToDelete);

        try {
            get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Response(getBody(), getStatusCode());
    }
}
