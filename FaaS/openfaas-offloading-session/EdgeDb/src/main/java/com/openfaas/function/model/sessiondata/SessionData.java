package com.openfaas.function.model.sessiondata;

import com.google.gson.Gson;

public class SessionData {

    public SessionRecord[] session_data;

    public String toJSON() {
        return new Gson().toJson(this);
    }
}
