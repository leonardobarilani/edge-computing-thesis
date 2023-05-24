package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.function.api.NonBlockingOffloadable;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

public class Handler extends NonBlockingOffloadable {

    public IResponse HandleNonBlockingOffload(IRequest req) {
        Response res = new Response();
        res.setContentType("video/mp4");
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, X-session, X-request-id");


        String requestedFile = req.getQuery().get("file");

        if(requestedFile == null) {
            String message = "{\"statusCode\":\"400\",\"message\":\"400 Missing query parameter 'file' in url\"}";
            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
            return res;
        }
        String file = EdgeDB.get(requestedFile);
        if(file == null) {
            String message = "{\"statusCode\":\"404\",\"message\":\"404 File '" + requestedFile + "' not found\"}";
            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(404);
            return res;
        }
        System.out.println("Successfully returned file '" + requestedFile + "'");
        res.setStatusCode(200);
        res.setBody(file);

	    return res;
    }
}
