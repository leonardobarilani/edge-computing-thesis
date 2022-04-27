import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class StateFulFunction {

    private String proprietaryLocation;
    private String currentLocation;
    private String functionName;
    private boolean expired;
    private String sessionId;
    private String username;
    private String password;
    private boolean log;

    public StateFulFunction() { }

    public void init (String username, String password, String location, String functionName) throws Exception {
        this.proprietaryLocation = location;
        this.currentLocation = location;
        this.functionName = functionName;
        this.expired = false;
        this.username = username;
        this.password = password;
        String uri =
                "http://" + this.username + ":" + this.password + "@" + this.proprietaryLocation + ":31112/" +
                        "function/session-offloading-manager?command=create-session&function=" + this.functionName;
        log("Sending GET: " + uri);
        this.sessionId = sendGET(uri).body();
        log("Session received: " + this.sessionId);
    }

    public String call (String body) throws ExpiredSessionException, InvalidProtocolException, IOException, InterruptedException {
        if (expired)
            throw new ExpiredSessionException();
        String uri =
            "http://" + username + ":" + password + "@" + currentLocation + ":31112" +
            "/function/session-offloading-manager?command=call-function";

        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .setHeader("X-Function-Session", sessionId)
                .build();

        log("Sending POST: " + uri);
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        log("Response: \n\t" +
                response.uri() + "\n\t" +
                response.headers() + "\n\t" +
                response.statusCode() + "\n\t" +
                response.body());
        String functionResult = response.headers().allValues("X-Function-Result").get(0);

        switch (functionResult)
        {
            case "success":
                return response.body();
            case "failure":
                currentLocation = proprietaryLocation;
                return call(body);
            case "offload":
                currentLocation = response.headers().allValues("X-Offload").get(0);
                return call(body);
            default:
                throw new InvalidProtocolException();
        }
    }

    public void close () {
        expired = true;
        try {
            sendGET("http://" + username + ":" + password + "@" + proprietaryLocation + ":31112/function/delete-session?session=" + sessionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HttpResponse<String> sendGET(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .GET()
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void setLog(boolean log) {
        this.log = log;
    }
    
    public void log(String s) {
        if (log)
            System.out.println(s);
    }
}
