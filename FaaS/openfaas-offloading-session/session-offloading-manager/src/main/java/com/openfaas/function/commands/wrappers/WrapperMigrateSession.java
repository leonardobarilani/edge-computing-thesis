package com.openfaas.function.commands.wrappers;

public class WrapperMigrateSession extends HTTPWrapper {

    private String sessionToMigrate;
    private String typeOfMigrate;

    public WrapperMigrateSession() {
        super();
    }

    public WrapperMigrateSession gateway(String gateway) {
        this.setGateway(gateway);
        return this;
    }

    public WrapperMigrateSession sessionToMigrate(String session) {
        this.sessionToMigrate = session;
        return this;
    }

    public WrapperMigrateSession typeSessionData() {
        this.typeOfMigrate = "sessionData";
        return this;
    }

    public WrapperMigrateSession typeRequestIds() {
        this.typeOfMigrate = "requestIds";
        return this;
    }

    @Override
    public Response call() {
        setRemoteFunction("/function/session-offloading-manager-migrate-session?" +
                "command=migrate-session&" +
                "session=" + sessionToMigrate + "&" +
                "data-type=" + typeOfMigrate);

        try {
            get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Response(getBody(), getStatusCode());
    }
}
