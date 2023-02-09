package com.openfaas.function.commands;

import com.openfaas.function.commands.annotations.RequiresHeaderAnnotation;
import com.openfaas.function.commands.wrappers.WrapperOffloadSession;
import com.openfaas.function.daos.ConfigurationDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.function.utils.MigrateUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

import java.util.Base64;

@RequiresHeaderAnnotation.RequiresHeader(header = "X-session-token")
public class OffloadSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        String sessionJson = new String(Base64.getDecoder().decode(req.getHeader("X-session-token")));
        String offloading = ConfigurationDAO.getOffloading();
        if (offloading.equals("accept")) {
            // offload accepted
            System.out.println("Offloading accepted");

            SessionToken newSession = MigrateUtils.migrateSessionFromRemoteToLocal(sessionJson);

            res.setStatusCode(200);
            res.setBody("Offloaded:\n\tOld session: " + sessionJson + "\n\tNew session: " + newSession.getJson());
        } else {
            // offload redirected to parent
            System.out.println("Offloading not accepted");
            System.out.println("Redirecting session to parent:\n\t" + EdgeInfrastructureUtils.getParentLocationId() + "\n\t" + sessionJson);

            // call parent node to offload the session
            SessionToken sessionToOffload = SessionToken.Builder.buildFromJSON(sessionJson);

            new WrapperOffloadSession()
                    .gateway(EdgeInfrastructureUtils.getParentHost())
                    .sessionToOffload(sessionToOffload)
                    .call();
        }
    }
}
