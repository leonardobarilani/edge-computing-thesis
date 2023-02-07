package com.openfaas.function.commands.wrappers;

import okhttp3.Response;
import okhttp3.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Hashtable;

abstract class OkHTTPWrapper {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    private String bodyPOST = null;
    private int statusCode;
    private String body;
    private String gateway;
    private String remoteFunction;

    private Hashtable<String, String> headers = new Hashtable<>();

    void setBodyPOSTRequest(String body) {
        this.bodyPOST = body;
    }

    void setGateway (String gateway) {
        this.gateway = gateway;
    }

    void setRemoteFunction (String remoteFunction) {
        this.remoteFunction = remoteFunction;
    }

    void setHeader (String key, String value) {
        headers.put(key, value);
    }

    void get() throws Exception {

        String uri = gateway + remoteFunction;

        OkHttpClient client = new OkHttpClient();

        Request.Builder requestBuilder = new Request.Builder();
        System.out.println("Sending GET:\n\tURI:" + uri);
        // Add all the additional headers to the request
        for(var entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
            System.out.println("\tExtra header: " + entry.getKey() + " = " + entry.getValue());
        }
        Request request = requestBuilder
                .url(uri)
                .build();

        System.out.println("GET response: \n\tHeaders:");
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println("\n\t\t"+responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

            System.out.println("\tBody: "+response.body().string());
            statusCode = response.code();
            body = response.body().string();
        }
    }

    void post() throws Exception {
        String uri = gateway + remoteFunction;

        MediaType MEDIA_TYPE_JSON
                = MediaType.parse("text/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
            Request.Builder requestBuilder = new Request.Builder();

        System.out.println("Sending POST:\n\tURI:" + uri);
        // Add all the additional headers to the request
        for(var entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
            System.out.println("\tExtra header: " + entry.getKey() + " = " + entry.getValue());
        }


        Request request = requestBuilder
                    .url(uri)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, bodyPOST))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                System.out.println(response.body().string());

                statusCode = response.code();
                body = response.body().string();
        }
    }

    public abstract com.openfaas.function.commands.wrappers.Response call ();

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}