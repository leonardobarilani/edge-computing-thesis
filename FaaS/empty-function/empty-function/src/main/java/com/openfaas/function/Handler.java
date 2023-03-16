package com.openfaas.function;

import com.openfaas.function.api.Offloadable;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

public class Handler extends Offloadable {

    public IResponse HandleOffload(IRequest req) {
        Response res = new Response();
	    res.setBody("Hello, world!");

	    return res;
    }
}
