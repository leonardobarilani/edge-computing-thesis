# Offloading draft

TODO:

* Move `String EdgeInfrastructureUtils.getParentHost()` to Deployer jar. Should be an env variable created in `faas-cli deploy`.
* Completely hide internal serialization/deserialization type (json) from function calls (SessionToken.initJson())
* Replace json.simple with Gson
* Implement iterative (node-up-to-root) onload-session

## Edge API

### Commons

* SessionToken:

```JSON
{
    "session":"id_of_the_session", 
    "proprietaryLocation":"location_id", 
    "currentLocation":"location_id",
    "function": "function_name"
}
```

* RedisHandler
* EdgeInfrastructure/HTTP Utils (+ Response)

### Client

| Function         | Parameters                         | Return                                                                          | Deployed on | Called by                         | Description                                                                                                                                                                                                                                                                                                                                        |
|------------------|------------------------------------|---------------------------------------------------------------------------------|-------------|-----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `create-session` | `function=function_name`           | `session_id`                                                                    | Leaf        | Clients                           | Gives a client a unique session token                                                                                                                                                                                                                                                                                                              |
| `delete-session` | `session=<session_id>`             |                                                                                 | All         | Clients                           | Delete a session token                                                                                                                                                                                                                                                                                                                             |
| `call-function`  | `X-Function-Session: <session_id>` | `X-Function-Result: <success,failure,offload>` `X-Offload: <offload_location>`  | All         | Clients or user-defined functions | Calls a function. If `X-Function-Result: success` the response contains the return value of the function. If `X-Function-Result: failure` the node doesn't know where the function is (it has been unloaded; contact the proprietary location). If `X-Function-Result: offload` the function has been offloaded to `X-Offload: <offload_location>` |

### Oracle

| Function             | Parameters               | Return | Deployed on     | Called by       | Description                                                                                                                          |
|----------------------|--------------------------|--------|-----------------|-----------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `set-offload-status` | `status=<accept\reject>` |        | All except root | Metrics oracle  | Arbitrarily set the offloading status of the local node. `reject` means that the node will forward the offloading to the parent node |
| `force-offload`      |                          |        | All except root | Metrics oracle  | The local node has to offload a random function                                                                                      |
| `force-onload`       |                          |        | All except root | Metrics oracle  | The local node has to call his parent's `onload-session`                                                                             |

### Offloading

| Function          | Parameters             | Return                 | Deployed on      | Called by                           | Description                                                                                                                                                                                                                                                                                                                                   |
|-------------------|------------------------|------------------------|------------------|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `offload-session` | JSON of session object |                        | All except leafs | Nodes that are offloading a session | Called to offload a session. The receiving node can decide if offload the session also or accept it based on the current offloading status. If the local node actually accept the offloading it has to update the session object and perform the migration from `currentLocation` to itself and update `proprietaryLocation`'s session object |                                                                                                                                                                                                                                                                                     |
| `onload-session`  |                        | JSON of session object | All except leafs | All except root                     | Children call it on parent to unload sessions, if the parent has some sessions coming from the child subtree. Returns an arbitrary number of sessions objects                                                                                                                                                                                 |
| `update-session`  | JSON of session object |                        | Leaf             | All except leafs                    | The local node has just accepted an offload, so it has to update the session object of the leaf                                                                                                                                                                                                                                               |

### Debug

| Function          | Parameters             | Return                            | Deployed on | Called by                           | Description                                                                                                                                                                                                                                                                                                                                   |
|-------------------|------------------------|-----------------------------------|-------------|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `test-function`   | `session=<session_id>` | `offloaded: yes (location_id)/no` | All         | All excepts root                    | Clients call it on leafs. Children call it on parents                                                                                                                                                                                                                                                                                         |

## Client API

Clients outside the edge network have to "initialize" functions (if they are stateful functions) in order to call them

```Java
class Client {
    public static void main(String[] args) {
        StateFulFunction function = StateFulFunction.init(localhost, "functionName");
        response = function.call(request);
        function.close();
    }
}
```

| Function | Parameters                             | Return                      | Description                                                                                                                                                                                               |
|----------|----------------------------------------|-----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `init`   | Proprietary IP/Location, function name | `StateFulFunction` instance | Calls `create-session`, stores the session_id and the url to call the function. The url is always `http://<proprietaryLocation>/session-offloading-manager?command=get-location&function=<function_name>` |
| `call`   | Request                                | Response                    |                                                                                                                                                                                                           |
| `close`  |                                        |                             | Calls `delete-session` and invalidate this instance                                                                                                                                                       |

`call`: calls the function with the session id (HTTP custom header `X-Function-Session`). Has to follow custom redirects (`X-Function-Result: offload` or `X-Function-Result: failure`) if the function is offloaded or onloaded (from current `/session-offloading-manager` to offloaded/proprietary `/session-offloading-manager`)

Edge behaviour:

* If local: request internally forwarded to `http://<current_node>/functions/<function_name>`
* If not-present and proprietary: (`X-Function-Result: offload`) redirect to offload node
* If not-present and not-proprietary: (`X-Function-Result: failure`) will request again to proprietary

Edge Middleware:

1. A stateful function can call `StateFulFunction.getSessionId()` (just return the value of `X-Function-Session`)

Or maybe:

1. The RedisHandler directly extracts the session id from the request and the programmer doesn't have to handle anything directly
