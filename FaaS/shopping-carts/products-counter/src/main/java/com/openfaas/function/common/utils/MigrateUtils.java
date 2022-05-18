package com.openfaas.function.common.utils;

import com.google.gson.Gson;
import com.openfaas.function.common.RedisHandler;
import com.openfaas.function.common.sessiondata.SessionData;

public class MigrateUtils {

    public static void callRemoteMigrate(String fromLocation, String sessionId){
        String urlCurrentLocation = fromLocation +
                "/function/session-offloading-manager?command=migrate-session&session=" + sessionId;
        SessionData data = new Gson().fromJson(HTTPUtils.sendGET(urlCurrentLocation), SessionData.class);
        RedisHandler redisSessionData = new RedisHandler(RedisHandler.SESSIONS_DATA);
        redisSessionData.setSessionData(sessionId, data);
    }
}
