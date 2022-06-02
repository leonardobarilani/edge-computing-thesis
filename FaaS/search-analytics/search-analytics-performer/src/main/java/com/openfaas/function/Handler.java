package com.openfaas.function;

import com.openfaas.function.api.EdgeDB;
import com.openfaas.model.IHandler;
import com.openfaas.model.IResponse;
import com.openfaas.model.IRequest;
import com.openfaas.model.Response;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * search-analytics-performer API:
 *  /search-analytics-performer
 *      Body Response: human-readable list of searches
 */
public class Handler extends com.openfaas.model.AbstractHandler {

    public IResponse Handle(IRequest req) {
        Response res = new Response();
        System.out.println("------------BEGIN SEARCH ANALYTICS PERFORMER------------");

        long ttl = 60L * 60L * 4L; // 4 hours
        ttl = 60L * 5L; // 5 minutes
        // Current time in minutes
        long currentLocalDatabase =
                Calendar.getInstance().getTimeInMillis() / (1000L * 60L);

        Map<String, Integer> searchesCounter = new HashMap<>();
        // Access all the sessions of the last 4 hours
        for(long t = 0;t < ttl;t++)
        {
            String currentMinute = Long.toString(currentLocalDatabase - t);

            System.out.println("About to access local database: "+currentMinute);

            // Count searches
            EdgeDB db = new EdgeDB(currentMinute);
            var searchesList = db.getList("searches_list");
            // If the local database is expired, getList() will return null
            if (searchesList != null)
                searchesList.forEach(s -> {
                    if (searchesCounter.containsKey(s))
                        searchesCounter.put(s, searchesCounter.get(s) + 1);
                    else
                        searchesCounter.put(s, 1);
                });
            db.close();
        }

        String totalSearches = searchesCounter.entrySet().toString();
        System.out.println("Total Searches: \n"+totalSearches);
        res.setBody(totalSearches);
        System.out.println("------------END SEARCH ANALYTICS PERFORMER------------");
        return res;
    }
}
