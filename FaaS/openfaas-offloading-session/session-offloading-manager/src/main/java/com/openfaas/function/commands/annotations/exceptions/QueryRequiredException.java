package com.openfaas.function.commands.annotations.exceptions;

public class QueryRequiredException extends Exception {

    public QueryRequiredException(String message) {
        super(message);
    }
}
