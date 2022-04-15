package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.JedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

public class CreateSession implements Command {

    public void Handle(IRequest req, IResponse res) {
	    SessionToken sessionToken = new SessionToken();
        sessionToken.init();

        JedisHandler redis = new JedisHandler();

        String sessionJson = new Gson().toJson(sessionToken);
        redis.set(sessionToken.session, sessionJson);

        System.out.println("New session created: \n\t" + sessionJson);

        res.setBody(sessionToken.session);
        res.setStatusCode(200);
    }
}
