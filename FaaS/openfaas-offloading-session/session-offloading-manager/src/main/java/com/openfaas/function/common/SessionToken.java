package com.openfaas.function.common;

import com.google.gson.Gson;

public class SessionToken {
	public String session;
    public String proprietaryLocation;
    public String currentLocation;

    public SessionToken () { }

    public void init(String sessionName) {
        session = sessionName;
        proprietaryLocation = System.getenv("LOCATION_ID");
        currentLocation = System.getenv("LOCATION_ID");
    }

    public void initJson(String json) {
        SessionToken newSession = new Gson().fromJson(json, SessionToken.class);
        this.session = newSession.session;
        this.proprietaryLocation = newSession.proprietaryLocation;
        this.currentLocation = newSession.currentLocation;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }
}
