package com.openfaas.function.model.sessiondata;

public class SessionRecord {
    public String key;
    public String data;

    public SessionRecord(String key, String data) {
        this.key = key;
        this.data = data;
    }
}
