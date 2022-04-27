package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

public class OnloadSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler();

        // get a random session to onload
        SessionToken randomSession = new Gson().fromJson(redis.getRandom(), SessionToken.class);

        if (randomSession == null)
        {
            System.out.println("Node is empty, can't force an unload");
            res.setStatusCode(400);
            res.setBody("Node is empty, can't force an unload");
        }
        else
        {
            // remove the local reference of the session
            redis.delete(randomSession.session);

            String unloadedSession = new Gson().toJson(randomSession);
            System.out.println("Onloading:\n\t" + unloadedSession);

            res.setStatusCode(200);
            res.setBody(unloadedSession);
        }
        redis.close();
    }
}
