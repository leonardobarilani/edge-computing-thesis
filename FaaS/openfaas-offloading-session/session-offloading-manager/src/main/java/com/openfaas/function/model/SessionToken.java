package com.openfaas.function.model;

import com.google.gson.Gson;

public class SessionToken {

    public enum Fields {
        PROPRIETARY_LOCATION("PROPRIETARY_LOCATION"),
        CURRENT_LOCATION("CURRENT_LOCATION"),
        STATUS("STATUS");
        final String field;
        Fields(String field) {
            this.field = field;
        }
    }

    public enum Status {
        LOCKED("LOCKED"),
        UNLOCKED("UNLOCKED");
        final String status;
        Status(String status) {
            this.status = status;
        }
    }

	public String session;
    public String proprietaryLocation;
    public String currentLocation;
    public Status status;

    public SessionToken () { }

    public SessionToken init(String sessionName) {
        session = sessionName;
        proprietaryLocation = System.getenv("LOCATION_ID");
        currentLocation = System.getenv("LOCATION_ID");
        status = Status.LOCKED;
        return this;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }

    public static class Builder {

        public static SessionToken buildFromJSON (String json) {
            return new Gson().fromJson(json, SessionToken.class);
        }

        public static SessionToken buildFromSessionToken (SessionToken sessionToken) {
            SessionToken copySessionToken = new SessionToken();
            copySessionToken.session = sessionToken.session;
            copySessionToken.currentLocation = sessionToken.currentLocation;
            copySessionToken.proprietaryLocation = sessionToken.proprietaryLocation;
            copySessionToken.status = sessionToken.status;
            return copySessionToken;
        }
    }
}
