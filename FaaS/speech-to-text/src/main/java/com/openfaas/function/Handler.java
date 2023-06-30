package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.function.api.Offloadable;
import com.openfaas.function.utils.Logger;
import  com.openfaas.model.*;

import java.util.ArrayList;

public class Handler extends Offloadable {

    public IResponse HandleOffload(IRequest req) {
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

        try {
            var history = EdgeDB.getList("history");
            if (history == null)
                history = new ArrayList<>();
            history.add(text);
            EdgeDB.setList("history", history);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log("(HandlOffload) Error while saving history :(\n(HandlOffload) Text: " + text);
        }

        res.setBody("{\"message\":" + text + ", \"statusCode\":200}");
        res.setStatusCode(200);

        return res;
    }
}
