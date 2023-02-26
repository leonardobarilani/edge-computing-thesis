package com.openfaas.function.model;

import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;

public class SessionToken {

    public String session;
    public String proprietaryLocation;
    public String currentLocation;
    public String timestampCreation;
    public String timestampLastAccess;

    public SessionToken() {
    }

    public SessionToken init(String sessionName) {
        session = sessionName;
        proprietaryLocation = System.getenv("LOCATION_ID");
        currentLocation = System.getenv("LOCATION_ID");
        String now = ISODateTimeFormat.dateTimeNoMillis().print(DateTime.now(DateTimeZone.UTC));
        timestampCreation = now;
        timestampLastAccess = now;
        return this;
    }

    public String getJson() {
        return new Gson().toJson(this);
    }

    public String getJsonLocationsOnly() {
        SessionToken token = new SessionToken();
        token.session = this.session;
        token.proprietaryLocation = this.proprietaryLocation;
        token.currentLocation = this.currentLocation;
        token.timestampCreation = null;
        token.timestampLastAccess = null;
        return new Gson().toJson(token);
    }

    public enum Fields {
        PROPRIETARY_LOCATION("PROPRIETARY_LOCATION"),
        CURRENT_LOCATION("CURRENT_LOCATION"),
        TIMESTAMP_CREATION("TIMESTAMP_CREATION"),
        TIMESTAMP_LAST_ACCESS("TIMESTAMP_LAST_ACCESS");
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
            copySessionToken.timestampCreation = sessionToken.timestampCreation;
            copySessionToken.timestampLastAccess = sessionToken.timestampLastAccess;
            return copySessionToken;
        }
    }
}
