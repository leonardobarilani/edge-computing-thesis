package com.openfaas.function.commands.wrappers;

public class Response {

    String body;
    int statusCode;

    public Response (String body, int statusCode) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public String getBody () {
        return this.body;
    }

    public int getStatusCode () {
        return this.statusCode;
    }
}
