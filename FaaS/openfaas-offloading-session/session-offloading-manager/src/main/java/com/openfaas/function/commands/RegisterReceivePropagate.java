package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresQueryAnnotation;
import com.openfaas.function.daos.ConfigurationDAO;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

@RequiresQueryAnnotation.RequiresQuery(query = "function")
public class RegisterReceivePropagate implements ICommand {

    @Override
    public void Handle(IRequest req, IResponse res) {
        String function = req.getQuery().get("function");

        System.out.println("Request to register function: " + function);

        System.out.println("Registering function...");

        ConfigurationDAO.addReceivingFunction(function);

        System.out.println("Function registered!");

        res.setStatusCode(200);
    }
}
