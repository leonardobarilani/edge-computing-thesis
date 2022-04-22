package com.openfaas.function.common;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HTTPUtils {

    private HTTPUtils() { }

    public static Response sendGET(String urlString, String body){
        // https://www.baeldung.com/java-http-request
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(body);
            out.flush();
            out.close();

            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            int responseCode = con.getResponseCode();
            con.disconnect();
            return new Response(responseCode, response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void sendGETWithoutResponse(String urlString, String body){
        // https://www.baeldung.com/java-http-request
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(body);
            out.flush();
            out.close();
            con.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CompletableFuture<HttpResponse<String>> sendAsyncJsonPOST(String uri,
                                                                            String body)
            throws IOException
    {
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}















