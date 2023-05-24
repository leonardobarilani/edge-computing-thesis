package com.openfaas.function.commands;

import com.openfaas.function.daos.ConfigurationDAO;
import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.daos.SessionsDataDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.function.utils.Logger;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Request;
import com.openfaas.model.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class OffloadTrigger implements ICommand {

    /*
     * sessionsPerMemory and sessionsPerAccessTimestamp should
     * be populated directly in redis instead of
     * dumping the whole metadata db
     */

    HashMap<String, SessionToken> sessions;
    // TODO implement as a maxheap
    HashMap<Long, List<String>> sessionsPerMemory;
    // TODO implement as a minheap
    HashMap<String, List<String>> sessionsPerAccessTimestamp;
    long usedMemory;

    /*
     * Top threshold triggers the offload process.
     * When the bottom threshold is reached,
     * the offload process can stop.
     */
    long offloadTopThreshold;
    long offloadBottomThreshold;
    /*
     * Onload threshold triggers the onload process.
     * We just onload 1 session per triggered onload process
     * (aka every time the cron job execute this function)
     */
    long onloadThreshold;

    public void Handle(IRequest req, IResponse res) {
        // returnValue == 0: nothing happened
        // returnValue == -1: a session has been onloaded
        // returnValue > 0: number of offloaded bytes
        long returnValue = 0;

        populateData();

        Logger.log("Current status: \n" +
                "Used memory: " + usedMemory + "\n" +
                "OffloadTopThreshold: " + offloadTopThreshold + "\n" +
                "OffloadBottomThreshold: " + offloadBottomThreshold + "\n" +
                "OnloadThreshold: " + onloadThreshold);
        if (usedMemory >= offloadTopThreshold) {
            Logger.log("Triggered offload");
            returnValue = manageOffload();
        } else if (usedMemory <= onloadThreshold) {
            Logger.log("Triggered onload");
            manageOnload();
            returnValue = -1;
        } else {
            Logger.log("Node is healthy, nothing is triggered");
        }
        res.setBody(Long.toString(returnValue));
        res.setStatusCode(200);
    }

    private void populateData() {
        sessions = new HashMap<>();
        sessionsPerMemory = new HashMap<>();
        sessionsPerAccessTimestamp = new HashMap<>();
        usedMemory = 0;
        offloadTopThreshold = ConfigurationDAO.getOffloadTopThreshold();
        offloadBottomThreshold = ConfigurationDAO.getOffloadBottomThreshold();
        onloadThreshold = ConfigurationDAO.getOnloadThreshold();

        List<String> sessionsKeys = SessionsDataDAO.getAllSessionsIds();

        for (var sessionId : sessionsKeys) {
            SessionToken session = SessionsDAO.getSessionToken(sessionId);
            sessions.put(sessionId, session);

            // add to memory usage map
            long memoryUsage = SessionsDataDAO.getMemoryUsage(sessionId);
            if (sessionsPerMemory.containsKey(memoryUsage)) {
                var list = sessionsPerMemory.get(memoryUsage);
                list.add(sessionId);
            } else {
                List list = new ArrayList();
                list.add(sessionId);
                sessionsPerMemory.put(memoryUsage, list);
            }

            // add to total memory counter
            usedMemory += memoryUsage;

            // add to last access map
            String accessTimestamp = session.timestampLastAccess;
            if (sessionsPerAccessTimestamp.containsKey(accessTimestamp)) {
                var list = sessionsPerAccessTimestamp.get(accessTimestamp);
                list.add(sessionId);
            } else {
                List list = new ArrayList();
                list.add(sessionId);
                sessionsPerAccessTimestamp.put(accessTimestamp, list);
            }
        }
    }

    private long manageOffload() {
        long freedMemory = 0;
        long memoryCurrentSession = 0;
        List<String> sessionsToBeOffloaded = new ArrayList<>();

        // offload sessions until we have freed enough space
        while (usedMemory - freedMemory >= offloadBottomThreshold) {

            // find the sessions with the highest memory consumption
            if (sessionsToBeOffloaded.isEmpty()) {
                Long maxMemoryUsage = Collections.max(sessionsPerMemory.keySet());
                sessionsToBeOffloaded.addAll(sessionsPerMemory.get(maxMemoryUsage));
                memoryCurrentSession = maxMemoryUsage;
            }

            // offload just the first session in the list
            // (we check the condition of the loop for every single offloaded session)
            String sessionToOffload = sessionsToBeOffloaded.get(0);
            sessionsToBeOffloaded.remove(0);
            freedMemory += memoryCurrentSession;
            // TODO spawn a process to handle the offload instead of blocking this function
            // TODO this is very ugly. force-onload and force-offload
            //  should be decoupled from the http logic
            //  (eg: ForceOffloadHttpHandler, ForceOffloadService)
            new ForceOffload()
                    .Handle(
                            new Request(
                                    null,
                                    Collections.singletonMap("X-forced-session", sessionToOffload)
                            ),
                            new Response()
                    );

            // clean the offloaded session from the local metadata
            sessionsPerMemory
                    .get(memoryCurrentSession)
                    .remove(sessionToOffload);
            if (sessionsPerMemory.get(memoryCurrentSession).isEmpty())
                sessionsPerMemory.remove(memoryCurrentSession);

            String sessionLastAccess = sessions.get(sessionToOffload).timestampLastAccess;
            sessionsPerAccessTimestamp
                    .get(sessionLastAccess)
                    .remove(sessionToOffload);
            if (sessionsPerAccessTimestamp.get(sessionLastAccess).isEmpty())
                sessionsPerAccessTimestamp.remove(sessionLastAccess);

            sessions.remove(sessionToOffload);
        }
        return freedMemory;
    }

    private void manageOnload() {
        // TODO this is very ugly. force-onload and force-offload
        //  should be decoupled from the http logic
        //  (eg: ForceOffloadHttpHandler, ForceOffloadService)
        new ForceOnload().Handle(new Request(null, null), new Response());
    }
}
