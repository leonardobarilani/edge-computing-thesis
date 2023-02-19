package com.openfaas.function.model;

import com.google.gson.Gson;

public class SessionToken {

    public String session;
    public String proprietaryLocation;
    public String currentLocation;

    public SessionToken() {
    }

    public SessionToken init(String sessionName) {
        session = sessionName;
        proprietaryLocation = System.getenv("LOCATION_ID");
        currentLocation = System.getenv("LOCATION_ID");
        return this;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }

    public enum Fields {
        PROPRIETARY_LOCATION("PROPRIETARY_LOCATION"),
        CURRENT_LOCATION("CURRENT_LOCATION");
        final String field;

        Fields(String field) {
            this.field = field;
        }
    }

    public static class Builder {

        public static SessionToken buildFromJSON(String json) {
            return new Gson().fromJson(json, SessionToken.class);
        }

        public static SessionToken buildFromSessionToken(SessionToken sessionToken) {
            SessionToken copySessionToken = new SessionToken();
            copySessionToken.session = sessionToken.session;
            copySessionToken.currentLocation = sessionToken.currentLocation;
            copySessionToken.proprietaryLocation = sessionToken.proprietaryLocation;
            return copySessionToken;
        }
    }
}
