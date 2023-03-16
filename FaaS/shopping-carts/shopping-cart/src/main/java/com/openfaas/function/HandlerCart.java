package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

public class HandlerCart {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        System.out.println("\n\n\n--------BEGIN CART--------");

        String newProduct = req.getQuery().get("product");
        if (newProduct != null) {
            System.out.println("Adding product to the cart: " + newProduct);
            var list = EdgeDB.getList("products");
            list.add(newProduct);
            EdgeDB.setList("products", list);
            System.out.println("Propagating: " + newProduct);

            res.setStatusCode(200);

            new CounterWrapper()
                    .product(newProduct)
                    .call();
            // db.propagate(newProduct, "city", "products-counter");
        } else {
            res.setStatusCode(500);
        }
        String currentCart = EdgeDB.getList("products").toString();
        res.setBody(currentCart);
        System.out.println("Current cart: " + currentCart);

        System.out.println("--------END CART--------");
        return res;
    }
}
