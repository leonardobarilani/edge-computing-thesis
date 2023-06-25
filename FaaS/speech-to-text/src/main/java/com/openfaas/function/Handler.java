package com.openfaas.function;

import  com.openfaas.model.*;

public class Handler extends AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        
        if (req.getBody() == null) {
            res.setBody("{\"message\":\"400 Missing body in the request\", \"statusCode\":400}");
            res.setStatusCode(400);
            return res;
        }
        
        String file = req.getBody();
        String text = "";
        try {
            text = TranscriberDemo.speechToText(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        res.setBody("{\"message\":" + text + ", \"statusCode\":200}");
        res.setStatusCode(200);

        return res;
    }
}
