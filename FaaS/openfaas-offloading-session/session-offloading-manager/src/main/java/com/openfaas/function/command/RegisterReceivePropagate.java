package com.openfaas.function.command;

import com.openfaas.function.common.RedisHandler;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

public class RegisterReceivePropagate implements ICommand {

    @Override
    public void Handle(IRequest req, IResponse res) {
        String function = req.getQuery().get("function");

        System.out.println("Request to register function: " + function);

        RedisHandler redis = new RedisHandler(RedisHandler.RECEIVE_PROPAGATE_FUNCTIONS);

        System.out.println("Registering function...");

        redis.addReceivingFunction(function);

        System.out.println("Function registered!");

        res.setStatusCode(200);
    }
}
