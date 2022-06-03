package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

/**
 * video-footage-receiver api:
 * POST /video-footage-receiver
 *  Body: Image (just a float in the demo)
 *  X-session: camera_id
 */
public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        System.out.println("--------BEGIN VIDEO FOOTAGE RECEIVER--------");

        float crowdStatus = analyzeCrowdStatus(req.getBody());
        String cameraId = req.getHeader("X-session");

        System.out.println("CrowdStatus: "+crowdStatus+"\nCameraId: "+cameraId);

        EdgeDB db = new EdgeDB(req);
        db.propagate(
                cameraId + ":" + crowdStatus,
                "city",
                "store-crowdness");
        db.close();

        System.out.println("--------END VIDEO FOOTAGE RECEIVER--------");
	    return res;
    }

    private float analyzeCrowdStatus(String videoFootageData) {
        // TODO here there would be the algorithm that actually analyse the image to obtain a crowdness level

        float crowdness = Float.parseFloat(videoFootageData);

        if(crowdness > 1.0f)
            crowdness = 1.0f;
        else if(crowdness < 0.0f)
            crowdness = 0.0f;

        return crowdness;
    }
}
