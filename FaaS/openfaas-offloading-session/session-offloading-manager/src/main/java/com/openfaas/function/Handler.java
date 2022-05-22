package com.openfaas.function;

import com.openfaas.function.command.*;
import com.openfaas.model.*;

import java.util.HashMap;
import java.util.Map;

public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        try {

            System.out.println("\n\n----------BEGIN NEW COMMAND----------");
            System.out.println("Query raw: " + req.getQueryRaw());
            for (var v : req.getQuery().keySet())
                System.out.println("Key: " + v + ". Value: " + req.getQuery().get(v));
            System.out.println("Headers: " + req.getHeaders());

            String command = req.getQuery().get("command");

            Map<String, ICommand> commands = new HashMap<>();

            // Offloading API
            commands.put("force-offload", new ForceOffload());
            commands.put("force-onload", new ForceOnload());
            commands.put("offload-session", new OffloadSession());
            commands.put("onload-session", new OnloadSession());
            commands.put("update-session", new UpdateSession());
            commands.put("set-offload-status", new SetOffloadStatus());
            commands.put("migrate-session", new MigrateSession());

            // Debug API
            commands.put("test-function", new TestFunction());
            commands.put("redis", new Redis());

            // Propagate API
            commands.put("receive-propagate", new ReceivePropagate());
            commands.put("register-receive-propagate", new RegisterReceivePropagate());

            if (commands.containsKey(command)) {
                System.out.println("----------BEGIN COMMAND <" + command + "> HANDLE----------");
                commands.get(command).Handle(req, res);
                System.out.println("----------END COMMAND HANDLE----------");
            } else {
                res.setBody("Unrecognized command: " + command + "\nAvailable commands: " + commands.keySet());
                res.setStatusCode(404);
            }
            System.out.println("----------END NEW COMMAND----------");

        } catch(Exception e) { e.printStackTrace(); }
	    return res;
    }
}
