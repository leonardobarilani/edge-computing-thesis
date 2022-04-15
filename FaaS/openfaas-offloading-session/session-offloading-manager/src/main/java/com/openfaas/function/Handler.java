package com.openfaas.function;

import com.openfaas.function.command.*;
import com.openfaas.model.*;

import java.util.HashMap;
import java.util.Map;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();

        /*
        System.out.println("----------DETECTED ENV----------");
        System.out.println("Location id: " + System.getenv("LOCATION_ID"));
        System.out.println("Redis host: " + System.getenv("REDIS_HOST"));
        System.out.println("Redis port: " + System.getenv("REDIS_PORT"));
        System.out.println("Redis Password: " + System.getenv("REDIS_PASSWORD"));*/

        System.out.println("----------NEW COMMAND----------");
        System.out.println("Query raw: " + req.getQueryRaw());
        //System.out.println("Query key set: " + req.getQuery().keySet());
        for(var v : req.getQuery().keySet())
            System.out.println("Query key: " + v + ". Value: " + req.getQuery().get(v));

        String command = req.getQuery().get("command");

        Map<String, Command> commands = new HashMap<>();
        
        commands.put("delete-session", new DeleteSession());
        commands.put("force-offload", new ForceOffload());
        commands.put("force-unload", new ForceUnload());
        commands.put("receive-session", new ReceiveSession());
        commands.put("create-session", new CreateSession());
        commands.put("set-offload-status", new SetOffloadStatus());
        commands.put("test-function", new TestFunction());
        commands.put("unload-session", new UnloadSession());
        commands.put("update-session", new UpdateSession());

        if (commands.containsKey(command))
        {
            System.out.println("----------START HANDLE----------");
            commands.get(command).Handle(req, res);
            System.out.println("----------END HANDLE----------");
        }
        else
        {
            res.setBody("Unrecognized command: " + command + "\nAvailable commands: " + commands.keySet());
            res.setStatusCode(404);
        }

	    return res;
    }
}
