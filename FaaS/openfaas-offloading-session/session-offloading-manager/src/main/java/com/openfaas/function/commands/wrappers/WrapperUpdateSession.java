package com.openfaas.function.commands.wrappers;

import com.openfaas.function.model.SessionToken;

public class WrapperUpdateSession extends HTTPWrapper {

    private SessionToken session = null;

    public WrapperUpdateSession() {
        super();
    }

    public WrapperUpdateSession gateway (String gateway) {
        this.setGateway(gateway);
        return this;
    }

    public WrapperUpdateSession sessionToUpdate(SessionToken session) {
        this.session = session;
        return this;
    }

    @Override
    public Response call() {
        setRemoteFunction("/function/session-offloading-manager?command=update-session");
        setBodyPOSTRequest(session.getJson());
        try {
            post ();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Response(getBody(), getStatusCode());
    }
}
