package com.openfaas.function.utils;

public class Logger {

    public static void log(String message) {
        if(message.length() > 1024) {
            message = message.substring(0, 1024);
        }
        System.out.println(message);
    }
}
