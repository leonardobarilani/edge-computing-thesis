package com.openfaas.function.utils;

import com.google.gson.Gson;
import com.openfaas.function.commands.wrappers.Response;
import com.openfaas.function.commands.wrappers.WrapperMigrateSession;
import com.openfaas.function.commands.wrappers.WrapperUpdateSession;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.daos.SessionsDataDAO;
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

        // TODO maybe replace this code with https://redis.io/commands/migrate/
        // migrate data from the location that has the session data, to this node
        String fromLocation = EdgeInfrastructureUtils.getGateway(migrateFrom);
        String sessionToMigrate = sessionToken.session;
        System.out.println("Migrating session from:\n\t" + fromLocation);

        Response response = new WrapperMigrateSession()
                .gateway(fromLocation)
                .sessionToMigrate(sessionToMigrate)
                .call();

        SessionData data = new Gson().fromJson(response.getBody(), SessionData.class);
        SessionsDataDAO.setSessionData(sessionToMigrate, data);

        // send new json object to proprietaryLocation
        System.out.println("Updating proprietary:\n\t" + sessionToken.proprietaryLocation + "\n\t" + sessionToken.getJson());
        new WrapperUpdateSession()
                .gateway(EdgeInfrastructureUtils.getGateway(sessionToken.proprietaryLocation))
                .sessionToUpdate(sessionToken)
                .call();

        // release the lock now that the session is completely migrated and updated
        SessionsDAO.unlockSession(sessionToken.session);

        return sessionToken;
    }
}
