package com.openfaas.function.command;

import com.google.gson.Gson;
import com.openfaas.function.common.PropagateData;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.utils.EdgeInfrastructureUtils;
import com.openfaas.function.common.utils.HTTPUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReceivePropagate implements ICommand {
    @Override
    public void Handle(IRequest req, IResponse res) {
        PropagateData data = new Gson().fromJson(req.getBody(), PropagateData.class);

        System.out.println("Received propagate: " + req.getBody());

        RedisHandler redis = new RedisHandler(RedisHandler.RECEIVE_PROPAGATE_FUNCTIONS);

        System.out.println("Forwarding value to all registered functions...");

        List<CompletableFuture<HttpResponse<String>>> functions = new LinkedList<>();
        var receivingFunctions = redis.getAllReceivingFunctions();

        redis.close();

        receivingFunctions
                .parallelStream()
                .filter(functionName -> functionName.equals(data.function))
                .forEach(functionName -> {
                        String uri = EdgeInfrastructureUtils.getGateway(System.getenv("LOCATION_ID")) +
                                "/function/" + functionName;

                        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                                .POST(HttpRequest.BodyPublishers.ofString(data.value))
                                .build();

                        System.out.println("Forwarding to: " + uri);

                        functions.add(HttpClient.newBuilder()
                            .followRedirects(HttpClient.Redirect.NORMAL)
                            .build()
                            .sendAsync(request, HttpResponse.BodyHandlers.ofString()));
        });
        functions.forEach(CompletableFuture::join);
    }
}
