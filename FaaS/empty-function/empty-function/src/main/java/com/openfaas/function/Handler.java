package com.openfaas.function;

import  com.openfaas.model.*;
import com.openfaas.function.api.Offloadable;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Handler extends Offloadable {

    public IResponse HandleOffload(IRequest req) {
        Response res = new Response();
        try {
            res.setBody(Inet4Address.getLocalHost().getHostAddress() +"\n"+ InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return res;
    }
}
