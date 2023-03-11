package com.openfaas.function.commands.wrappers;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class HTTPWrapper {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    private final Hashtable<String, String> headers = new Hashtable<>();
    private String bodyPOST = null;
    private int statusCode;
    private String body;
    private String gateway;
    private String remoteFunction;
    private Map<String, List<String>> responseHeaders;

    protected void setBodyPOSTRequest(String body) {
        this.bodyPOST = body;
    }

    protected void setGateway(String gateway) {
        this.gateway = gateway;
    }

    protected void setRemoteFunction(String remoteFunction) {
        this.remoteFunction = remoteFunction;
    }

    protected void setHeader(String key, String value) {
        headers.put(key, value);
    }

    protected void get() throws Exception {
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
        responseHeaders = response.headers().map();

        System.out.println("(HTTPWrapper.get) Response: ");
        System.out.println("(HTTPWrapper.get) \tStatusCode: " + statusCode);
        System.out.println("(HTTPWrapper.get) \tBody: " + body);
    }

    protected void post() throws Exception {
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
        responseHeaders = response.headers().map();

        System.out.println("(HTTPWrapper.post) Response: ");
        System.out.println("(HTTPWrapper.post) \tStatusCode: " + statusCode);
        System.out.println("(HTTPWrapper.post) \tBody: " + body);
    }

    public abstract Response call();

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }

    public String getResponseHeader(String header) {
        return responseHeaders.get(header).get(0);
    }
}
