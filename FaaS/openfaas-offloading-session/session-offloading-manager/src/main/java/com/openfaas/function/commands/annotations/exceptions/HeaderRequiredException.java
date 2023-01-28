package com.openfaas.function.commands.annotations.exceptions;

public class HeaderRequiredException extends Exception {

    public HeaderRequiredException(String message) {
        super(message);
    }
}
