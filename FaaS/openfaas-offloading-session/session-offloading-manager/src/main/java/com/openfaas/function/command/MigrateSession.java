package com.openfaas.function.command;

import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.sessiondata.SessionData;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

public class MigrateSession implements ICommand {

    @Override
    public void Handle(IRequest req, IResponse res) {

        String sessionId = req.getQuery().get("session");

        System.out.println("About to migrate Session Id: " + sessionId);

        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS_DATA);

        SessionData data = redis.getSessionData(sessionId);

        res.setBody(data.toJSON());
        res.setStatusCode(200);

        redis.deleteSession(sessionId);

        redis.close();
    }
}
