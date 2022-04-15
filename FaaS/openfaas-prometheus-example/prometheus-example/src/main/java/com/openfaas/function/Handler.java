package com.openfaas.function;

import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();

        // https://www.baeldung.com/java-http-request

        String urlString = "http://prometheus.openfaas:9090/api/v1/query?query=";

        String query = req.getBody();

        urlString += query;

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            BufferedWriter outputStream = new BufferedWriter(new
                    OutputStreamWriter(con.getOutputStream()));
            outputStream.write(query);
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        int status = -1;
        try {
            status = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        StringBuilder response = new StringBuilder();
        /*response.append("Contacting prometheus on: ")
                .append(urlString)
                .append(" (").append(url.getHost()).append(")\n")
                .append("Response: ").append(status).append("\n");
*/
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        con.disconnect();

        res.setBody(response.toString());
	    return res;
    }
}
