package com.openfaas.function.commands;

import com.openfaas.function.daos.ConfigurationDAO;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.function.utils.HTTPUtils;
import com.openfaas.function.utils.MigrateUtils;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;

import java.io.IOException;

public class OffloadSession implements ICommand {

    public void Handle(IRequest req, IResponse res) {
        String offloading = ConfigurationDAO.getOffloading();
        if (offloading.equals("accept"))
        {
            // offload accepted
            System.out.println("Offloading accepted");

            String sessionJson = req.getBody();
            String jsonNewSession = MigrateUtils.migrateSession(sessionJson);

            res.setStatusCode(200);
            res.setBody("Offloaded:\n\tOld session: " + sessionJson + "\n\tNew session: " + jsonNewSession);
        }
        else
        {
            // offload redirected to parent
            System.out.println("Offloading not accepted");

            // call parent node to offload the session
            String url = EdgeInfrastructureUtils.getParentHost() +
                    "/function/session-offloading-manager?command=offload-session";
            System.out.println("Redirecting session to parent:\n\t" + url + "\n\t" + req.getBody());
            try {
                HTTPUtils.sendAsyncJsonPOST(url, req.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
