package com.openfaas.function.utils;

import com.google.gson.Gson;
import com.openfaas.function.daos.RedisHandler;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.model.sessiondata.SessionData;

import java.io.IOException;

public class MigrateUtils {

    /**
     * Migrate the given session from the currentLocation of the session to this node
     * @param sessionJson session token json of the session
     * @return the new session token json
     */
    public static String migrateSession (String sessionJson) {
        // update json object
        SessionToken sessionToken = new SessionToken();
        sessionToken.initJson(sessionJson);
        String migrateFrom = sessionToken.currentLocation;
        sessionToken.currentLocation = System.getenv("LOCATION_ID");
        String jsonNewSession = sessionToken.getJson();

        // save new json object in redis
        RedisHandler redis = new RedisHandler(RedisHandler.SESSIONS);
        redis.set(sessionToken.session, jsonNewSession);
        redis.close();

        // send new json object to proprietaryLocation
        String url = EdgeInfrastructureUtils.getGateway(sessionToken.proprietaryLocation) +
                "/function/session-offloading-manager?command=update-session";
        System.out.println("Updating proprietary:\n\t" + url + "\n\t" + jsonNewSession);
        try {
            HTTPUtils.sendAsyncJsonPOST(url, jsonNewSession);
        } catch (IOException e) { e.printStackTrace(); }

        // TODO maybe replace this code with https://redis.io/commands/migrate/
        // migrate data from the location that has the session data, to this node
        String fromLocation = EdgeInfrastructureUtils.getGateway(migrateFrom);
        String sessionToMigrate = sessionToken.session;
        String urlCurrentLocation = fromLocation +
                "/function/session-offloading-manager?command=migrate-session&session=" + sessionToMigrate;
        System.out.println("Migrating session from:\n\t" + fromLocation);
        SessionData data = new Gson().fromJson(HTTPUtils.sendGET(urlCurrentLocation), SessionData.class);
        RedisHandler redisSessionData = new RedisHandler(RedisHandler.SESSIONS_DATA);
        redisSessionData.setSessionData(sessionToMigrate, data);

        return jsonNewSession;
    }
}
