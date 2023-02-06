package com.openfaas.function;

import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();

        res.setStatusCode(307);
        res.setHeader("Location", "http://172.27.30.130:31112/function/echo");

        return res;
    }
}
