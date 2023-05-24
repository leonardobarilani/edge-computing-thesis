package com.openfaas.function.utils;

import com.google.gson.Gson;
import com.openfaas.function.commands.wrappers.Response;
import com.openfaas.function.commands.wrappers.WrapperMigrateSession;
import com.openfaas.function.commands.wrappers.WrapperUpdateSession;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.daos.SessionsDataDAO;
import com.openfaas.function.daos.SessionsRequestsDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.model.sessiondata.SessionData;

public class MigrateUtils {

    /**
     * Migrate the given session from the currentLocation of the session to this node
     *
     * @param sessionJson session token json of the session
     * @return the new session token json
     */
    public static SessionToken migrateSessionFromRemoteToLocal(String sessionJson) {
        // update session token with the local
        SessionToken sessionToken = SessionToken.Builder.buildFromJSON(sessionJson);
        String migrateFrom = sessionToken.currentLocation;
        sessionToken.currentLocation = System.getenv("LOCATION_ID");

        // save new json object in redis
        SessionsDAO.setSessionToken(sessionToken);

        // (1/3) prepare for migration of data from the location that has the session data, to this node
        String fromLocation = EdgeInfrastructureUtils.getGateway(migrateFrom);
        String sessionToMigrate = sessionToken.session;
        Logger.log("Migrating session from:\n\t" + fromLocation);

        // (2/3) migrate session data
        Response responseSessionData = new WrapperMigrateSession()
                .gateway(fromLocation)
                .sessionToMigrate(sessionToMigrate)
                .typeSessionData()
                .call();
        SessionData sessionData = new Gson().fromJson(responseSessionData.getBody(), SessionData.class);
        SessionsDataDAO.setSessionData(sessionToMigrate, sessionData);

        // (3/3) migrate request ids
        Response responseRequestIds = new WrapperMigrateSession()
                .gateway(fromLocation)
                .sessionToMigrate(sessionToMigrate)
                .typeRequestIds()
                .call();
        String[] requestIds = responseRequestIds.getBody().trim().split("\\s*,\\s*");
        SessionsRequestsDAO.addSessionRequests(sessionToMigrate, requestIds);

        // send new json object to proprietaryLocation
        Logger.log("Updating proprietary:\n\t" + sessionToken.proprietaryLocation + "\n\t" + sessionToken.getJson());
        new WrapperUpdateSession()
                .gateway(EdgeInfrastructureUtils.getGateway(sessionToken.proprietaryLocation))
                .sessionToUpdate(sessionToken)
                .call();

        return sessionToken;
    }
}
