package com.openfaas.function.commands;

import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

public interface ICommand {

    void Handle(IRequest req, IResponse res);
}
