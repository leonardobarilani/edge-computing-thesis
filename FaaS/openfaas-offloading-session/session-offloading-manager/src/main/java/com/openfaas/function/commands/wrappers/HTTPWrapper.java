package com.openfaas.function.commands.wrappers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Hashtable;

abstract class HTTPWrapper {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    private final Hashtable<String, String> headers = new Hashtable<>();
    private String bodyPOST = null;
    private int statusCode;
    private String body;
    private String gateway;
    private String remoteFunction;

    void setBodyPOSTRequest(String body) {
        this.bodyPOST = body;
    }

    void setGateway(String gateway) {
        this.gateway = gateway;
    }

    void setRemoteFunction(String remoteFunction) {
        this.remoteFunction = remoteFunction;
    }

    void setHeader(String key, String value) {
        headers.put(key, value);
    }

    void get() throws Exception {
        String uri = gateway + remoteFunction;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .setHeader("User-Agent", "session-offloading-manager");

        System.out.println("Sending GET:\n\tURI:" + uri);
        // Add all the additional headers to the request
        for (var entry : headers.entrySet()) {
            requestBuilder.setHeader(entry.getKey(), entry.getValue());
            System.out.println("\tExtra header: " + entry.getKey() + " = " + entry.getValue());
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        statusCode = response.statusCode();
        body = response.body();
    }

    void post() throws Exception {
        String uri = gateway + remoteFunction;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(bodyPOST))
                .uri(URI.create(uri))
                .setHeader("User-Agent", "session-offloading-manager")
                .header("Content-Type", "application/json");

        System.out.println("Sending POST:\n\tURI:" + uri);
        // Add all the additional headers to the request
        for (var entry : headers.entrySet()) {
            requestBuilder.setHeader(entry.getKey(), entry.getValue());
            System.out.println("\tExtra header: " + entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("\tBody: " + bodyPOST);

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        statusCode = response.statusCode();
        body = response.body();
    }

    public abstract Response call();

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
