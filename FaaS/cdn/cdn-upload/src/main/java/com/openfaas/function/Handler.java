package com.openfaas.function;

import com.google.gson.Gson;
import com.openfaas.function.api.EdgeDB;
import com.openfaas.function.api.Offloadable;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

public class Handler extends Offloadable {

    public IResponse HandleOffload(IRequest req) {
        Response res = new Response();
        String body = req.getBody();

        if(body == null) {
            String message = "{\"statusCode\":\"400\",\"message\":\"400 Missing body in http message\"}";
            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
            return res;
        }
        FileData file = new Gson().fromJson(body, FileData.class);
        if(file == null) {
            String message = "{\"statusCode\":\"400\",\"message\":\"400 Json in body message is not parsable\"}";
            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
            return res;
        }
        if(file.fileName == null) {
            String message = "{\"statusCode\":\"400\",\"message\":\"400 Field fileName is missing\"}";
            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
            return res;
        }
        if(file.fileData == null) {
            String message = "{\"statusCode\":\"400\",\"message\":\"400 Field fileData is missing\"}";
            System.out.println(message);
            res.setBody(message);
            res.setStatusCode(400);
            return res;
        }
        EdgeDB.set(file.fileName, file.fileData);
        System.out.println("Successfully uploaded file to EdgeDB class");
        res.setStatusCode(200);

	    return res;
    }
}
