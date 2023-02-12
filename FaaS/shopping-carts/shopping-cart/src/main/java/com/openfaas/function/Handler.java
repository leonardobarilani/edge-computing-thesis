package com.openfaas.function;

import com.openfaas.function.api.Offloadable;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

/*
 * shopping-cart API:
 * shopping-cart?product=<new-product>

 * If product is not present, no product will be added
 * The response body is the products currently in the cart
 * */

public class Handler extends Offloadable {

    public IResponse HandleOffload(IRequest req) {

        if ("counter".equals(System.getenv("TYPE")))
            return new HandlerCounter().Handle(req);
        else
            return new HandlerCart().Handle(req);
    }
}
