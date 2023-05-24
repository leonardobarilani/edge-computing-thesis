package com.openfaas.function.commands.wrappers;

import com.openfaas.function.model.SessionToken;

import java.util.Base64;

public class WrapperOffloadSession extends HTTPWrapper {

    private SessionToken session = null;

    public WrapperOffloadSession() {
        super();
    }

    public WrapperOffloadSession gateway(String gateway) {
        this.setGateway(gateway);
        return this;
    }

    public WrapperOffloadSession sessionToOffload(SessionToken session) {
        this.session = session;
        return this;
    }

    @Override
    public Response call() {
        setRemoteFunction("/function/session-offloading-manager?command=offload-session");
        setHeader("X-session-token", Base64.getEncoder().encodeToString(session.getJson().getBytes()));
        try {
            get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Response(getBody(), getStatusCode());
    }
}
