package com.openfaas.function.commands;

import com.openfaas.function.commands.wrappers.Response;
import com.openfaas.function.commands.wrappers.WrapperOnloadSession;
import com.openfaas.function.daos.SessionsLocksDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.function.utils.Logger;
import com.openfaas.function.utils.MigrateUtils;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;

import java.util.List;

public class ForceOnload implements ICommand {

    private String randomValue;
    private String remoteHost;

    public void Handle(IRequest req, IResponse res) {
        /* --------- Try to find a session to onload --------- */
        String sessionJson = onloadMetadata();

        /* --------- Check if we have a session to onload --------- */
        if (sessionJson == null) {
            String message = "Unable to provide a valid session";
            Logger.log(message);
            res.setBody(message);
            res.setStatusCode(400);
            return;
        }

        /* --------- Lock the session locally --------- */
        String sessionId = SessionToken.Builder.buildFromJSON(sessionJson).session;
        while (!acquireLock(res, sessionId)) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        /* --------- Perform the onload --------- */
        SessionToken newSession = MigrateUtils.migrateSessionFromRemoteToLocal(sessionJson);

        /* --------- Release locks --------- */
        releaseRemoteLock(sessionId);
        releaseLock(res, sessionId);

        String message = "Unloaded:\n" +
                "Old session: " + SessionToken.Builder.buildFromJSON(sessionJson).getJsonLocationsOnly() + "\n" +
                "New session: " + newSession.getJsonLocationsOnly();
        Logger.log(message);
        res.setBody(message);
        res.setStatusCode(200);
    }

    private String onloadMetadata() {
        List<String> availableNodes = EdgeInfrastructureUtils.getLocationsFromNodeToLevel(
                System.getenv("LOCATION_ID"), EdgeInfrastructureUtils.infrastructure.areaTypesIdentifiers[0]);
        if (availableNodes.size() > 0)
            availableNodes.remove(availableNodes.size() - 1);

        String sessionJson = null;
        String nodeToOnloadFrom;
        boolean foundOnloadableSession = false;
        // Ask for a session in the nodes until we find it or there are no more nodes to ask for it
        while (!foundOnloadableSession && !availableNodes.isEmpty()) {
            nodeToOnloadFrom = availableNodes.get(availableNodes.size() - 1);

            Logger.log("Trying to onload from: " + nodeToOnloadFrom);

            remoteHost = EdgeInfrastructureUtils.getGateway(nodeToOnloadFrom);
            WrapperOnloadSession wrapper = new WrapperOnloadSession();
            Response response = wrapper
                    .gateway(remoteHost)
                    .actionGetSession()
                    .call();
            if (response.getStatusCode() != 200) {
                Logger.log("No available sessions from: " + nodeToOnloadFrom);
                availableNodes.remove(availableNodes.size() - 1);
            } else {
                foundOnloadableSession = true;
                randomValue = wrapper.getRandomValue();
                sessionJson = response.getBody();
            }
        }

        return sessionJson;
    }

    private void releaseRemoteLock(String sessionId) {
        new WrapperOnloadSession()
                .gateway(remoteHost)
                .actionReleaseSession()
                .setSession(sessionId)
                .setRandomValue(randomValue)
                .call();
    }

    private boolean acquireLock(IResponse res, String session) {
        if (!SessionsLocksDAO.lockSession(session)) {
            Logger.log("Cannot acquire lock on session <" + session + ">");
            res.setStatusCode(400);
            res.setBody("Cannot acquire lock on session <" + session + ">");
            return false;
        }
        return true;
    }

    private boolean releaseLock(IResponse res, String session) {
        if (!SessionsLocksDAO.unlockSession(session)) {
            Logger.log("Cannot release lock on session <" + session + ">");
            res.setStatusCode(500);
            res.setBody("Cannot release lock on session <" + session + ">");
            return false;
        }
        return true;
    }
}
