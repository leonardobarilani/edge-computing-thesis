package com.openfaas.function.model;

import com.google.gson.Gson;

public class SessionToken {
	public String session;
    public String proprietaryLocation;
    public String currentLocation;

    public SessionToken () { }

    public SessionToken init(String sessionName) {
        session = sessionName;
        proprietaryLocation = System.getenv("LOCATION_ID");
        currentLocation = System.getenv("LOCATION_ID");
        return this;
    }

    public SessionToken initJson(String json) {
        SessionToken newSession = new Gson().fromJson(json, SessionToken.class);
        this.session = newSession.session;
        this.proprietaryLocation = newSession.proprietaryLocation;
        this.currentLocation = newSession.currentLocation;
        return this;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }
}
