package com.openfaas.function.common;

public class Response {
    public int code;
    public String body;
    public Response(int code, String body) { this.code = code; this.body = body; }
}