package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.function.api.Offloadable;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

/**
 * search-analytics-data-receivers API:
 * /search-analytics-data-receivers?search=<value>
 * X-session: <user>
 */
public class Handler extends Offloadable {

    public IResponse HandleOffload(IRequest req) {
        Response res = new Response();
        System.out.println("------------BEGIN SEARCH DATA RECEIVERS------------");

        String search = req.getQuery().get("search");
        EdgeDB db = new EdgeDB(req);
        db.propagate(search, "city", "search-analytics-store-data");
        db.close();

        res.setStatusCode(200);
        System.out.println("------------END SEARCH DATA RECEIVERS------------");
        return res;
    }
}
