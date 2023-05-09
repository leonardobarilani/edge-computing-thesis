package com.openfaas.function;

import  com.openfaas.model.*;

public class Handler extends AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        res.setBody("Hello, world!");

        return res;
    }
}
