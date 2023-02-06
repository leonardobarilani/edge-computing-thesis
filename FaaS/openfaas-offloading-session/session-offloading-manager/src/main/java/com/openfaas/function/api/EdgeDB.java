package com.openfaas.function.api;

import com.google.gson.Gson;
import com.openfaas.function.model.PropagateData;
import com.openfaas.function.utils.EdgeInfrastructureUtils;
import com.openfaas.model.IRequest;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;

public class EdgeDB implements IEdgeDB {

    private static final String SESSIONS_DATA = "2";

    private final String url;
    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;
    private final RedisCommands<String, String> syncCommands;
    private final String sessionId;

    public EdgeDB(IRequest req) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + SESSIONS_DATA;
        sessionId = req.getHeader("X-session");

        System.out.println("(EdgeDB.Constructor) X-session: <" + sessionId + "> Url: " + url);

        redisClient = RedisClient.create(url);
        redisClient.setDefaultTimeout(20, TimeUnit.SECONDS);
        connection = redisClient.connect();
        syncCommands = connection.sync();

        System.out.println("(EdgeDB.Constructor) Established connection");
    }

    public EdgeDB(String session) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + SESSIONS_DATA;
        sessionId = session;

        System.out.println("(EdgeDB.Constructor) Not-offloadable session: <" + sessionId + "> Url: " + url);

        redisClient = RedisClient.create(url);
        redisClient.setDefaultTimeout(20, TimeUnit.SECONDS);
        connection = redisClient.connect();
        syncCommands = connection.sync();

        System.out.println("(EdgeDB.Constructor) Established connection");

    }

    private static CompletableFuture<HttpResponse<String>> sendAsyncJsonPOST(String uri,
                                                                             String body)
            throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public void close() {
        connection.close();
        redisClient.shutdown();
    }

    public String get(String key) {
        System.out.println("(EdgeDB.get) (sessionId: " + sessionId + ") Redis get with key: " + key);
        return syncCommands.hget(sessionId, key);
    }

    public void set(String key, String value) {
        System.out.println("(EdgeDB.set) (sessionId: " + sessionId + ") Redis set with key, value: " + key + ", " + value);
        syncCommands.hset(sessionId, Map.ofEntries(entry(key, value)));
    }

    public List<String> getList(String key) {
        /*System.out.println("(EdgeDB.getList) (sessionId: "+sessionId+") Redis hexists with key: " + key);
        if (!syncCommands.hexists(sessionId, key))
            return null;*/
        System.out.println("(EdgeDB.getList) (sessionId: " + sessionId + ") Redis get with key: " + key);
        String rawList = syncCommands.hget(sessionId, key);
        if (rawList == null) {
            System.out.println("(EdgeDB.getList) (sessionId: " + sessionId + ") null value from Redis get with key: " + key);
            return null;
        }
        System.out.println("(EdgeDB.getList) (sessionId: " + sessionId + ") Parsing with Gson");
        return new Gson().fromJson(rawList, HList.class).list;

        //System.out.println("(EdgeDB) (sessionId: "+sessionId+") Redis zrangebyscore with key: " + key);
        //System.out.println("(EdgeDB) [WARNING] Lists still don't use the session");
        //return syncCommands.zrange(key, Long.MIN_VALUE, Long.MAX_VALUE);
        //return syncCommands.zrangebyscore(key, Range.create(0, Integer.MAX_VALUE));
    }

    /**
     * The old API had single elements of the list expire.
     * For the sake of the shopping-cart example we will not implement that.
     * Need further discussion
     *
     * @param key
     * @param value
     */
    public void addToList(String key, String value) {
        System.out.println("(EdgeDB.addToList) (sessionId: " + sessionId + ") Redis hexists with key: " + key);
        if (!syncCommands.hexists(sessionId, key)) {
            HList list = new HList();
            list.list.add(value);
            System.out.println("(EdgeDB.addToList) (sessionId: " + sessionId + ") Parsing with Gson.toJson");
            String newJsonList = new Gson().toJson(list);
            System.out.println("(EdgeDB.addToList) (sessionId: " + sessionId + ") Redis set with key, value: " + key + ", " + newJsonList);
            syncCommands.hset(sessionId, Map.ofEntries(entry(key, newJsonList)));
            return;
        }
        System.out.println("(EdgeDB.addToList) (sessionId: " + sessionId + ") Redis get with key: " + key);
        String rawList = syncCommands.hget(sessionId, key);
        if (rawList == null) {
            System.out.println("(EdgeDB.addToList) (sessionId: " + sessionId + ") null value from Redis get with key: " + key);
            return;
        }
        System.out.println("(EdgeDB.addToList) (sessionId: " + sessionId + ") Parsing with Gson.fromJson");
        var list = new Gson().fromJson(rawList, HList.class);
        list.list.add(value);
        System.out.println("(EdgeDB.addToList) (sessionId: " + sessionId + ") Parsing with Gson.toJson");
        String newJsonList = new Gson().toJson(list);
        System.out.println("(EdgeDB.addToList) (sessionId: " + sessionId + ") Redis set with key, value: " + key + ", " + newJsonList);
        syncCommands.hset(sessionId, Map.ofEntries(entry(key, newJsonList)));

        //System.out.println("(EdgeDB) (sessionId: "+sessionId+") Redis zadd with key, value: " + key + ", " + value);
        //System.out.println("(EdgeDB) [WARNING] Lists still don't use the session");
        //syncCommands.zadd(key, Calendar.getInstance().getTimeInMillis(), value);
    }

    public void propagate(String value, String levelToPropagateTo, String function) {
        List<String> locationsToPropagateTo = EdgeInfrastructureUtils.getLocationsFromNodeToLevel(
                System.getenv("LOCATION_ID"),
                levelToPropagateTo
        );
        System.out.println("(EdgeDB.propagate) Propagating to locations " + locationsToPropagateTo + " with value: " + value);

        // calling all locations on location/session-offloading-manager?command=receive-propagate
        // Set Body: <value>
        List<CompletableFuture> futures = new ArrayList<>(locationsToPropagateTo.size());
        for (var l : locationsToPropagateTo)
            try {
                String uri = EdgeInfrastructureUtils.getGateway(l) + "/function/session-offloading-manager?command=receive-propagate";
                String json = new Gson().toJson(new PropagateData(value, function));
                System.out.println("(EdgeDB.propagate) Propagating now: \n\t" + uri + "\n\t" + json);
                futures.add(sendAsyncJsonPOST(
                        uri,
                        json
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }

        System.out.println("(EdgeDB.propagate) Waiting propagation responses");
        // wait for all the responses before continuing the execution
        futures.forEach(CompletableFuture::join);
        System.out.println("(EdgeDB.propagate) All propagation responses received");
    }

    public void setTTL(long seconds) {
        System.out.println("(EdgeDB.setTTL) (sessionId: " + sessionId + ") Setting TTL to: " + seconds + " seconds");
        syncCommands.expire(sessionId, seconds);
    }

    public void delete(String key) {
        syncCommands.hdel(sessionId, key);
    }

    private class HList {
        List<String> list = new ArrayList<>();
    }
}
