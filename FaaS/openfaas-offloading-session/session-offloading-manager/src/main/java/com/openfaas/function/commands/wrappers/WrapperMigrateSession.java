package com.openfaas.function.commands.wrappers;

public class WrapperMigrateSession extends HTTPWrapper {

    public WrapperMigrateSession() {
        super();
    }

    private String sessionToMigrate;

    public WrapperMigrateSession gateway (String gateway) {
        this.setGateway(gateway);
        return this;
    }

    public WrapperMigrateSession sessionToMigrate (String session) {
        this.sessionToMigrate = session;
        return this;
    }

    @Override
    public Response call() {
        setRemoteFunction("/function/session-offloading-manager?command=migrate-session&session=" + sessionToMigrate);

        try {
            get ();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Response(getBody(), getStatusCode());
    }
}
