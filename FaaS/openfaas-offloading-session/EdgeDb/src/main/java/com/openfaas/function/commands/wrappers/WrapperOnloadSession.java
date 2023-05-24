package com.openfaas.function.commands.wrappers;

public class WrapperOnloadSession extends HTTPWrapper {

    private String action;
    private String session;
    private String randomValue;

    public WrapperOnloadSession() {
        super();
    }

    public WrapperOnloadSession gateway(String gateway) {
        this.setGateway(gateway);
        return this;
    }

    public WrapperOnloadSession actionGetSession() {
        action = "get-session";
        return this;
    }

    public WrapperOnloadSession actionReleaseSession() {
        action = "release-session";
        return this;
    }

    public String getRandomValue() {
        return randomValue;
    }

    public WrapperOnloadSession setRandomValue(String randomValue) {
        this.randomValue = randomValue;
        return this;
    }

    public WrapperOnloadSession setSession(String session) {
        this.session = session;
        return this;
    }

    @Override
    public Response call() {
        if ("get-session".equals(action))
            setRemoteFunction("/function/session-offloading-manager?command=onload-session&action=" + action);
        else
            setRemoteFunction("/function/session-offloading-manager?command=onload-session&" +
                    "action=" + action + "&session=" + session + "&random-value=" + randomValue);
        setHeader("X-onload-location", System.getenv("LOCATION_ID"));
        try {
            get();
            if ("get-session".equals(action) && getStatusCode() == 200)
                this.randomValue = getResponseHeader("X-random-value");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new Response(getBody(), getStatusCode());
    }
}
