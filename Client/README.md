# Client API

Clients outside the edge network have to initialize stateful functions in order to call them

```Java
class Client {
    public static void main(String[] args) {
        StateFulFunction function = new StateFulFunction();
        function.init("username", "password", "location", "functionName");
        
        try {
            String requestBody = "Hello";
            String responseBody = function.call(request);
            System.out.println("Response received: " + responseBody);
            
            function.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

| Function | Parameters                             | Return                      | Description                                                                                                                                                                                                                                                                                                                  |
|----------|----------------------------------------|-----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `init`   | Proprietary IP/Location, function name | `StateFulFunction` instance | Calls `create-session`, stores the session_id and the url to call the function. The url is always `http://<proprietaryLocation>/session-offloading-manager?command=get-location&function=<function_name>`                                                                                                                    |
| `call`   | Request                                | Response                    | Calls the function with the session id (HTTP custom header `X-Function-Session`). Has to follow custom redirects (`X-Function-Result: offload` or `X-Function-Result: failure`) if the function is offloaded or onloaded (from current `/session-offloading-manager` to offloaded/proprietary `/session-offloading-manager`) |
| `close`  |                                        |                             | Calls `delete-session` and invalidate this instance                                                                                                                                                                                                                                                                          |
