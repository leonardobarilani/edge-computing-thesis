package com.openfaas.function.command;

import com.openfaas.function.common.EdgeInfrastructureUtils;
import com.openfaas.function.common.HTTPUtils;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.SessionToken;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

public class Redis implements Command {
    @Override
    public void Handle(IRequest req, IResponse res) {
        RedisHandler redis = new RedisHandler();

        String command = req.getQuery().get("redis-command");
        if (command == null)
            command = "";

        System.out.println("About to launch redis-command: " + command);

        switch(command) {
            case "delete-all-sessions":
                redis.deleteAll();
                System.out.println("Deleted all sessions");
                res.setBody("Deleted all sessions");
                res.setStatusCode(200);
                break;
            default:
                System.out.println("Command not recognized");
                res.setBody("Command not recognized");
                res.setStatusCode(400);
        }

        redis.close();
    }
}
