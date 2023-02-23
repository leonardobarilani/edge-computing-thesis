package com.openfaas.function.commands;

import com.openfaas.function.commands.wrappers.Response;
import com.openfaas.function.commands.wrappers.WrapperOnloadSession;
import com.openfaas.function.daos.SessionsLocksDAO;
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
        System.out.println("Onloading from:\n\t" + EdgeInfrastructureUtils.getParentLocationId(System.getenv("LOCATION_ID")));

        Response response = new WrapperOnloadSession()
                .gateway(EdgeInfrastructureUtils.getParentHost(System.getenv("LOCATION_ID")))
                .call();

        // } end while

        if (response.getStatusCode() != 200) {
            res.setStatusCode(400);
            System.out.println("/onload-session unable to provide a valid session");
            res.setBody("/onload-session unable to provide a valid session");
            return;
        }

        String sessionJson = response.getBody();

        SessionToken sessionToken = SessionToken.Builder.buildFromJSON(sessionJson);

        String sessionId = sessionToken.session;
        while(!acquireLock(res, sessionId))
        {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        SessionToken newSession = MigrateUtils.migrateSessionFromRemoteToLocal(sessionJson);

        releaseLock(res, sessionId);

        res.setStatusCode(200);
        res.setBody("Unloaded:\nOld session: " + sessionJson + "\nNew session: " + newSession.getJson());
    }

    private boolean acquireLock (IResponse res, String session) {
        if (!SessionsLocksDAO.lockSession(session))
        {
            System.out.println("Cannot acquire lock on session <" + session + ">");
            res.setStatusCode(400);
            res.setBody("Cannot acquire lock on session <" + session + ">");
            return false;
        }
        return true;
    }

    private boolean releaseLock (IResponse res, String session) {
        if (!SessionsLocksDAO.unlockSession(session))
        {
            System.out.println("Cannot release lock on session <" + session + ">");
            res.setStatusCode(500);
            res.setBody("Cannot release lock on session <" + session + ">");
            return false;
        }
        return true;
    }
}
