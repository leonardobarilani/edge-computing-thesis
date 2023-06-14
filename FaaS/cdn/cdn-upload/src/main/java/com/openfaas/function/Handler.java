package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.function.api.Offloadable;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

public class Handler extends Offloadable {

    //POST
    public IResponse HandleOffload(IRequest req) {
        Response res = new Response();
        String body = req.getBody();

        String requestedFile = req.getQuery().get("file");

        if(requestedFile == null) {
            String message = "{\"statusCode\":\"400\",\"message\":\"400 Missing query parameter 'file' in url\"}";
            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
            return res;
        }

        if(body == null) {
            String message = "{\"statusCode\":\"400\",\"message\":\"400 Missing body in http message\"}";
            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
            return res;
        }
        EdgeDB.set(requestedFile, body);
        System.out.println("Successfully uploaded file to EdgeDB class");
        res.setStatusCode(200);
	    return res;
    }
}
