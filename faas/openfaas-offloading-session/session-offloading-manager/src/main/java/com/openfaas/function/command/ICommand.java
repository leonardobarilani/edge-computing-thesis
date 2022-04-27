package com.openfaas.function.command;

import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

public interface ICommand {

    void Handle(IRequest req, IResponse res) ;
}
