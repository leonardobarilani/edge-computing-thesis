package com.openfaas.function.commands.annotations.exceptions;

public class BodyRequiredException extends Exception {

    public BodyRequiredException(String message) {
        super(message);
    }
}
