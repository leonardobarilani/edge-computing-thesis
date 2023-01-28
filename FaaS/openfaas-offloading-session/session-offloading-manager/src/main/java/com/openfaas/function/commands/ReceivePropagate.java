package com.openfaas.function.commands;

import com.google.gson.Gson;
import com.openfaas.function.commands.annotations.RequiresBodyAnnotation;
import com.openfaas.function.daos.ConfigurationDAO;
import com.openfaas.function.model.PropagateData;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiresBodyAnnotation.RequiresBody
public class ReceivePropagate implements ICommand {
    @Override
    public void Handle(IRequest req, IResponse res) {
        PropagateData data = new Gson().fromJson(req.getBody(), PropagateData.class);

        System.out.println("Received propagate: " + req.getBody());

        System.out.println("Forwarding value to all registered functions...");

        List<CompletableFuture<HttpResponse<String>>> functions = new LinkedList<>();
        var receivingFunctions = ConfigurationDAO.getAllReceivingFunctions();

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
        System.out.println("Forwarded to all registered functions");
    }
}
