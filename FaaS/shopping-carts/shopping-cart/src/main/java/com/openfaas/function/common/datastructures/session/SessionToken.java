package com.openfaas.function.common.datastructures.session;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Calendar;
import java.util.concurrent.ThreadLocalRandom;

public class SessionToken {
	public String session;
    public String proprietaryLocation;
    public String currentLocation;

    public SessionToken () { }

    public SessionToken (String json) {
        initJson(json);
    }

    public void init() {
        session = System.getenv("LOCATION_ID") + "_" +
                Calendar.getInstance().getTimeInMillis() + "_" +
                ThreadLocalRandom.current().nextInt();
        proprietaryLocation = System.getenv("LOCATION_ID");
        currentLocation = System.getenv("LOCATION_ID");
    }

    public void init(String name) {
        session = name;
        proprietaryLocation = System.getenv("LOCATION_ID");
        currentLocation = System.getenv("LOCATION_ID");
    }

    public void initJson(String json) {
        try {
            JSONObject jo = (JSONObject) new JSONParser().parse(json);
            session = (String) jo.get("session");
            proprietaryLocation = (String) jo.get("proprietaryLocation");
            currentLocation = (String) jo.get("currentLocation");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getJson() {
        // TODO change this to a proper gson/simplejson translation to avoid injections
        return
            "{\"session\":\"" + session + "\"," +
            "\"proprietaryLocation\":\"" + proprietaryLocation + "\"," +
            "\"currentLocation\":\"" + currentLocation + "\"}";
    }
}
