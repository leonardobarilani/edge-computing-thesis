package com.openfaas.function.command;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

public class Redis implements ICommand {
    @Override
    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis;

        String command = req.getQuery().get("redis-command");
        if (command == null)
            command = "";

        System.out.println("About to launch redis-command: " + command);

        switch(command) {
            case "delete-all-sessions":
                redis = new RedisHandler(RedisHandler.SESSIONS);
                redis.deleteAll();
                redis.close();
                redis = new RedisHandler(RedisHandler.SESSIONS_DATA);
                redis.deleteAll();
                redis.close();
                System.out.println("Deleted all sessions");
                res.setBody("Deleted all sessions");
                res.setStatusCode(200);
                break;
            case "get-session-data":
                redis = new RedisHandler(RedisHandler.SESSIONS_DATA);
                var response = redis.getSessionData (req.getQuery().get("session")).toJSON();
                System.out.println(response);
                res.setBody(response);
                res.setStatusCode(200);
                redis.close();
                break;
            case "init-session":
                redis = new RedisHandler(RedisHandler.SESSIONS);
                SessionToken token = new SessionToken();
                token.init(req.getHeader("X-session"));
                redis.set(token.session, token.getJson());
                redis.close();
                if (req.getQuery().get("key") != null && req.getQuery().get("value") != null)
                {
                    EdgeDB db = new EdgeDB(req.getHeader("X-session"));
                    db.set(req.getQuery().get("key"), req.getQuery().get("value"));
                    db.close();
                }
                res.setStatusCode(200);
                break;
            default:
                System.out.println("Command not recognized");
                res.setBody("Command not recognized");
                res.setStatusCode(400);
        }
    }
}
