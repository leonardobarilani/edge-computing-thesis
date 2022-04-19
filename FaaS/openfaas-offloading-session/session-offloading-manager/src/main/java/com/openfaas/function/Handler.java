package com.openfaas.function;

import com.openfaas.function.command.*;
import com.openfaas.model.*;

import java.util.HashMap;
import java.util.Map;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        try {

            System.out.println("----------NEW COMMAND----------");
            System.out.println("Query raw: " + req.getQueryRaw());
            for (var v : req.getQuery().keySet())
                System.out.println("Query key: " + v + ". Value: " + req.getQuery().get(v));

            String command = req.getQuery().get("command");

            Map<String, Command> commands = new HashMap<>();

            commands.put("create-session", new CreateSession());
            commands.put("delete-session", new DeleteSession());
            commands.put("force-offload", new ForceOffload());
            commands.put("force-onload", new ForceOnload());
            commands.put("offload-session", new OffloadSession());
            commands.put("onload-session", new OnloadSession());
            commands.put("update-session", new UpdateSession());
            commands.put("test-function", new TestFunction());
            commands.put("set-offload-status", new SetOffloadStatus());

            if (commands.containsKey(command)) {
                System.out.println("----------START HANDLE----------");
                commands.get(command).Handle(req, res);
                System.out.println("----------END HANDLE----------");
            } else {
                res.setBody("Unrecognized command: " + command + "\nAvailable commands: " + commands.keySet());
                res.setStatusCode(404);
            }

        } catch(Exception e) { e.printStackTrace(); }
	    return res;
    }
}
