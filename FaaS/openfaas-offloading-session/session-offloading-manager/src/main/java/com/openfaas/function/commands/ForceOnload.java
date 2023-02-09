package com.openfaas.function.commands;

import com.openfaas.function.commands.wrappers.Response;
import com.openfaas.function.commands.wrappers.WrapperOnloadSession;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.function.utils.MigrateUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

public class ForceOnload implements ICommand {

    public void Handle(IRequest req, IResponse res) {

        // TODO wrap this into a cycle to iterate until we reached the root or exit if we onloaded something
        // while {

        // call parent node to receive a session
        System.out.println("Onloading from:\n\t" + EdgeInfrastructureUtils.getParentLocationId());

        Response response = new WrapperOnloadSession()
                .gateway(EdgeInfrastructureUtils.getParentHost())
                .call();

        // } end while

        if (response.getStatusCode() != 200) {
            res.setStatusCode(400);
            System.out.println("/onload-session unable to provide a valid session");
            res.setBody("/onload-session unable to provide a valid session");
            return;
        }

        String sessionJson = response.getBody();
        SessionToken newSession = MigrateUtils.migrateSessionFromRemoteToLocal(sessionJson);

        res.setStatusCode(200);
        res.setBody("Unloaded:\nOld session: " + sessionJson + "\nNew session: " + newSession.getJson());
    }
}
