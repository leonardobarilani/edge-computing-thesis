package com.openfaas.function.api;

import com.google.gson.Gson;
import com.openfaas.function.common.datastructures.PropagateData;
import com.openfaas.function.common.utils.EdgeInfrastructureUtils;
import com.openfaas.function.common.utils.HTTPUtils;
import com.openfaas.model.IRequest;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;

public class EdgeDB {

    public static final String SESSIONS_DATA = "2";

    private final String url;
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> syncCommands;
    private final String sessionId;

    /**
     * The default constructor will use env variables for host, password and port.
     * The table used is the sessions_data table (table 2)
     */
    public EdgeDB(IRequest req) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + SESSIONS_DATA;
        sessionId = req.getHeader("X-session");

        System.out.println("(EdgeDB) (Constructor) X-session: <" + sessionId + "> Url: " + url);

        redisClient = RedisClient.create(url);
        redisClient.setDefaultTimeout(20, TimeUnit.SECONDS);
        connection = redisClient.connect();
        syncCommands = connection.sync();
    }
    public EdgeDB(String session) {
        String host = System.getenv("REDIS_HOST");
        String password = System.getenv("REDIS_PASSWORD");
        String port = System.getenv("REDIS_PORT");
        url = "redis://" + password + "@" + host + ":" + port + "/" + SESSIONS_DATA;
        sessionId = session;

        System.out.println("(EdgeDB) (Constructor) X-session: <" + sessionId + "> Url: " + url);

        redisClient = RedisClient.create(url);
        redisClient.setDefaultTimeout(20, TimeUnit.SECONDS);
        connection = redisClient.connect();
        syncCommands = connection.sync();
    }

    public void close() {
        connection.close();
        redisClient.shutdown();
    }

    public String get(String key){
        System.out.println("(EdgeDB) (sessionId: "+sessionId+") Redis get with key: " + key);
        return syncCommands.hget(sessionId, key);
    }
    public void set(String key, String value){
        System.out.println("(EdgeDB) (sessionId: "+sessionId+") Redis set with key, value: " + key + ", " + value);
        syncCommands.hset(sessionId, Map.ofEntries(entry(key, value)));
    }

    public List<String> getList(String key){
        System.out.println("(EdgeDB) (sessionId: "+sessionId+") Redis zrangebyscore with key: " + key);
        System.out.println("(EdgeDB) [WARNING] Lists still don't use the session");
        return syncCommands.zrange(key, Long.MIN_VALUE, Long.MAX_VALUE);
        //return syncCommands.zrangebyscore(key, Range.create(0, Integer.MAX_VALUE));
    }
    /**
     * The old API had single elements of the list expire.
     * For the sake of the shopping-cart example we will not implement that.
     * Need further discussion
     * @param key
     * @param value
     */
    public void addToList(String key, String value){
        System.out.println("(EdgeDB) (sessionId: "+sessionId+") Redis zadd with key, value: " + key + ", " + value);
        System.out.println("(EdgeDB) [WARNING] Lists still don't use the session");
        syncCommands.zadd(key, Calendar.getInstance().getTimeInMillis(), value);
    }

    public void propagate (String value, String levelToPropagateTo, String function) {
        List<String> locationsToPropagateTo = EdgeInfrastructureUtils.getLocationsFromNodeToLevel(
                System.getenv("LOCATION_ID"),
                levelToPropagateTo
        );
        System.out.println("(EdgeDB) (sessionId: "+sessionId+") Propagating to locations " + locationsToPropagateTo + " with value: " + value);

        // calling all locations on location/session-offloading-manager?command=receive-propagate
        // Set Body: <value>
        List<CompletableFuture> futures = new ArrayList<>(locationsToPropagateTo.size());
        for (var l : locationsToPropagateTo)
            try {
                String uri = EdgeInfrastructureUtils.getGateway(l) + "/function/session-offloading-manager?command=receive-propagate";
                String json = new Gson().toJson(new PropagateData(value, function));
                System.out.println("(EdgeDB) (sessionId: " + sessionId + ") Propagating now: \n\t" + uri + "\n\t" + json);
                futures.add(HTTPUtils.sendAsyncJsonPOST(
                        uri,
                        json
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }

        System.out.println("(EdgeDB) (sessionId: " + sessionId + ") Waiting propagation responses");
        // wait for all the responses before continuing the execution
        futures.forEach(CompletableFuture::join);
        System.out.println("(EdgeDB) (sessionId: " + sessionId + ") All propagation responses received");
    }

    public void delete(String key) {
        syncCommands.hdel(sessionId, key);
    }
}