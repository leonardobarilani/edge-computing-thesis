package com.openfaas.function;

import com.openfaas.function.commands.*;
import com.openfaas.function.commands.annotations.RequiresBodyAnnotation;
import com.openfaas.function.commands.annotations.RequiresHeaderAnnotation;
import com.openfaas.function.commands.annotations.RequiresQueryAnnotation;
import com.openfaas.function.commands.annotations.exceptions.BodyRequiredException;
import com.openfaas.function.commands.annotations.exceptions.HeaderRequiredException;
import com.openfaas.function.commands.annotations.exceptions.QueryRequiredException;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Response;

import java.util.HashMap;
import java.util.Map;

public class Handler extends com.openfaas.model.AbstractHandler {
    private static Map<String, ICommand> commands;

    public Handler() {
        commands = new HashMap<>();
        // Offloading API
        commands.put("force-offload", new ForceOffload());
        commands.put("force-onload", new ForceOnload());
        commands.put("set-offload-status", new SetOffloadStatus());

        // Offloading Internals
        commands.put("offload-session", new OffloadSession());
        commands.put("onload-session", new OnloadSession());
        commands.put("update-session", new UpdateSession());
        commands.put("migrate-session", new MigrateSession());

        // Debug API
        commands.put("test-function", new TestFunction());

        // Propagate API
        commands.put("receive-propagate", new ReceivePropagate());
        commands.put("register-receive-propagate", new RegisterReceivePropagate());
    }

    public IResponse Handle(IRequest req) {
        IResponse res = new Response();
        System.out.println("\n\n----------BEGIN NEW COMMAND----------");
        System.out.println("Query raw: " + req.getQueryRaw());
        try {
            for (var v : req.getQuery().keySet())
                System.out.println("Key: " + v + ". Value: " + req.getQuery().get(v));
            System.out.println("Headers: " + req.getHeaders());

            ICommand command = getCommand(req, res);
            if (command != null) {

                boolean annotationsAreValid = processAnnotations(req, res, command);
                if (annotationsAreValid) {

                    System.out.println("\n----------BEGIN COMMAND <" + req.getQuery().get("command") + "> HANDLE----------");
                    command.Handle(req, res);
                    System.out.println("----------END COMMAND HANDLE----------\n");
                }
            }
        } catch (Exception e) {
            String message = "500 Internal server error\n";
            res.setBody(message);
            res.setStatusCode(500);
            System.out.println(message);
            e.printStackTrace();
        }
        System.out.println("----------END NEW COMMAND----------\n\n");
	    return res;
    }

    private ICommand getCommand(IRequest req, IResponse res) {
        String command = req.getQuery().get("command");

        if (commands.containsKey(command)) {
            return commands.get(command);
        } else {
            String message = "Unrecognized command: " + command + "\nAvailable commands: " + commands.keySet();
            System.err.println(message);
            res.setBody(message);
            res.setStatusCode(404);
            return null;
        }
    }

    private boolean processAnnotations(IRequest req, IResponse res, ICommand command) {
        try {
            RequiresHeaderAnnotation.verify(command, req);
            RequiresQueryAnnotation.verify(command, req);
            RequiresBodyAnnotation.verify(command, req);
            return true;
        } catch (HeaderRequiredException | QueryRequiredException | BodyRequiredException e) {
            String message = "Error handling annotations: " + e.getMessage();
            System.out.println(message);
            e.printStackTrace();
            res.setStatusCode(400);
            res.setBody(message);
            return false;
        }
    }
}
