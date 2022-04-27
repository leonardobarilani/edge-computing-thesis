package com.openfaas.function.command;

import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

public class CreateSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler();
        SessionToken sessionToken = new SessionToken();
        sessionToken.init(req.getQuery().get("function"));
        String sessionJson = sessionToken.getJson();
        System.out.println("New session created: \n\t"+ sessionJson);

        redis.set(sessionToken.session, sessionJson);
        System.out.println("Session saved in Redis");

        res.setBody(sessionToken.session);
        res.setStatusCode(200);

        redis.close();
    }
}
