package com.openfaas.function.common;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HTTPUtils {


    public static int sendGET(String urlString){
        // https://www.baeldung.com/java-http-request
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
            outputStream.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder response = new StringBuilder();
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
        int responseCode = -1;
        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCode;
    }

    public static Response sendGET(String urlString, String body){
        // https://www.baeldung.com/java-http-request
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
            outputStream.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder response = new StringBuilder();
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
        int responseCode = -1;
        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response(responseCode, response.toString());
    }
}
