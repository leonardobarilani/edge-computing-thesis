package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.function.api.Offloadable;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

/*
* shopping-cart API:
* shopping-cart?product=<new-product>

* If product is not present, no product will be added
* The response body is the products currently in the cart
* */

public class Handler extends Offloadable {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        System.out.println("\n\n\n--------BEGIN CART--------");
        EdgeDB db = new EdgeDB(req);

        String newProduct = req.getQuery().get("product");
        if (newProduct != null)
        {
            System.out.println("Adding product to the cart: " + newProduct);
            db.addToList("products", newProduct);
            System.out.println("Propagating: " + newProduct);
            db.propagate(newProduct, "city", "products-counter");
        }
        String currentCart = db.getList("products").toString();
	    res.setBody(currentCart);
        System.out.println("Current cart: " + currentCart);

        System.out.println("--------END CART--------");
        db.close();
	    return res;
    }
}
