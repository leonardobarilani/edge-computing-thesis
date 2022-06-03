package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;
import org.ietf.jgss.GSSContext;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * get-less-crowded-path api:
 * GET get-less-crowded-path?startingPoint=<point>&destinationPoint=<point>
 *     Response body: list of arcs of the best path
 */
public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        System.out.println("--------BEGIN GET LESS CROWDED PATH--------");

        String startingPoint = req.getQuery().get("startingPoint");
        String destinationPoint = req.getQuery().get("destinationPoint");

        System.out.println("StartingPoint: "+startingPoint);
        System.out.println("DestinationPoint: "+destinationPoint);

        // TODO replace this with a get on redis of the camera ids
        var cameraIds = List.of("camera1", "camera2", "camera3");

        // Fetch cameras crowdness
        EdgeDB db = new EdgeDB("crowdness");
        var cameraCrowdness = new LinkedList<Float>();
        for(var cameraId : cameraIds)
            cameraCrowdness.add(Float.parseFloat(db.get(cameraId)));
        db.close();

        // Compute best path
        var computedPath = bestPath(cameraCrowdness);

        // Build the list of arcs of the best path
        StringBuilder builder = new StringBuilder();
        computedPath.forEach(s -> builder.append(s).append(","));
        res.setBody(builder.toString());
        res.setStatusCode(200);

        System.out.println("--------END GET LESS CROWDED PATH--------");
	    return res;
    }

    private List<String> bestPath(List<Float> cameraCrowdness) {
        // TODO implement a real bestPath
        long randomMillis = ThreadLocalRandom.current().nextInt(500);
        try {
            Thread.currentThread().wait(randomMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return List.of("arc1", "arc2", "arc3");
    }
}
