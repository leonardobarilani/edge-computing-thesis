package com.openfaas.function.commands;

import com.openfaas.function.daos.SessionsDAO;
import com.openfaas.function.daos.SessionsDataDAO;
import com.openfaas.function.model.SessionToken;
import com.openfaas.model.IRequest;
import com.openfaas.model.IResponse;
import com.openfaas.model.Request;
import com.openfaas.model.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class OffloadManager implements ICommand {

    /*
     * sessionsPerMemory and sessionsPerAccessTimestamp should
     * be populated directly in redis instead of
     * dumping the whole metadata db
     */

    HashMap<String, SessionToken> sessions = new HashMap<>();
    // TODO implement as a maxheap
    HashMap<Long, List<String>> sessionsPerMemory = new HashMap<>();
    // TODO implement as a minheap
    HashMap<String, List<String>> sessionsPerAccessTimestamp = new HashMap<>();
    long usedMemory;


    /*
     * Top threshold triggers the offload process.
     * When the bottom threshold is reached,
     * the offload process can stop.
     */
    // TODO move these env variables in the ConfigDAO (aka move them in redis)
    long offloadTopThreshold = Long.parseLong(System.getenv("OFFLOAD_TOP_THRESHOLD"));
    long offloadBottomThreshold = Long.parseLong(System.getenv("OFFLOAD_BOTTOM_THRESHOLD"));
    /*
     * Onload threshold triggers the onload process.
     * We just onload 1 session per triggered onload process
     * (aka every time the cron job execute this function)
     */
    long onloadThreshold = Long.parseLong(System.getenv("ONLOAD_THRESHOLD"));

    public void Handle(IRequest req, IResponse res) {
        populateData();

        System.out.println("Current status: \n" +
                "Used memory: " + usedMemory + "\n" +
                "OffloadTopThreshold: " + offloadTopThreshold + "\n" +
                "OffloadBottomThreshold: " + offloadBottomThreshold + "\n" +
                "OnloadThreshold: " + onloadThreshold);
        if (usedMemory >= offloadTopThreshold) {
            System.out.println("Triggered offload");
            manageOffload();
        } else if (usedMemory <= onloadThreshold) {
            System.out.println("Triggered onload");
            manageOnload();
        } else {
            System.out.println("Node is healthy, nothing is triggered");
        }
    }
    
    private void populateData() {
        List<String> sessionsKeys = SessionsDataDAO.getAllSessionsIds();

        usedMemory = 0;

        for(var sessionId : sessionsKeys) {
            SessionToken session = SessionsDAO.getSessionToken(sessionId);
            sessions.put(sessionId, session);

            // add to memory usage map
            long memoryUsage = SessionsDataDAO.getMemoryUsage(sessionId);
            if (sessionsPerMemory.containsKey(memoryUsage)) {
                var list = sessionsPerMemory.get(memoryUsage);
                list.add(sessionId);
            } else {
                sessionsPerMemory.put(memoryUsage, List.of(sessionId));
            }

            // add to total memory counter
            usedMemory += memoryUsage;

            // add to last access map
            String accessTimestamp = session.timestampLastAccess;
            if (sessionsPerAccessTimestamp.containsKey(accessTimestamp)) {
                var list = sessionsPerAccessTimestamp.get(accessTimestamp);
                list.add(sessionId);
            } else {
                sessionsPerAccessTimestamp.put(accessTimestamp, List.of(sessionId));
            }
        }
    }

    private void manageOffload () {
        long freedMemory = 0;
        long memoryCurrentSession = 0;
        List<String> sessionsToBeOffloaded = new ArrayList<>();

        // offload sessions until we have freed enough space
        while(usedMemory - freedMemory <= offloadBottomThreshold) {

            // find the sessions with the highest memory consumption
            if (sessionsToBeOffloaded.isEmpty()) {
                Long maxMemoryUsage = Collections.max(sessionsPerMemory.keySet());
                sessionsToBeOffloaded.addAll(sessionsPerMemory.get(maxMemoryUsage));
                memoryCurrentSession = maxMemoryUsage;
            }

            // offload just the first session in the list
            // (we check the condition of the loop for every single offloaded session)
            String sessionToOffload = sessionsToBeOffloaded.get(0);
            freedMemory += memoryCurrentSession;
            // TODO spawn a process to handle the offload instead of blocking this function
            // TODO this is very ugly. force-onload and force-offload
            //  should be decoupled from the http logic
            //  (eg: ForceOffloadHandler, ForceOffloadService)
            new ForceOffload()
                    .Handle(
                            new Request(
                                    null,
                                    Collections.singletonMap("X-forced-session", sessionToOffload)
                            ),
                            new Response()
                    );
        }
    }

    private void manageOnload () {
        // TODO this is very ugly. force-onload and force-offload
        //  should be decoupled from the http logic
        //  (eg: ForceOffloadHandler, ForceOffloadService)
        new ForceOnload().Handle(new Request(null, null), new Response());
    }
}
