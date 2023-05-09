package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

/**
 * store-crowdness api:
 * POST store-crowdness (receivePropagate)
 * Body: <cameraId>:<float [0, 1] to represent the crowdness>
 */
public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        System.out.println("--------BEGIN STORE CROWDNESS--------");

        int separator = req.getBody().indexOf(":");
        String cameraId = req.getBody().substring(0, separator);
        String crowdness = req.getBody().substring(separator + 1);

        System.out.println("CameraId: " + cameraId);
        System.out.println("Crowdness: " + crowdness);

        EdgeDB db = new EdgeDB("crowdness");
        db.set(cameraId, crowdness);
        db.close();

        System.out.println("--------END STORE CROWDNESS--------");
        return res;
    }
}
