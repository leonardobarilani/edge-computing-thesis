# Offloading draft

TODO:

* Add parameter <session> to /force-offload and /offload-session to allow the Oracle to offload a specific session
* Fix onload-session bug (example: A with children B and C. B offload to A. C call onload on A. A onload session of node B to node C, which is wrong)
* Implement iterative (parent-up-to-root) onload-session
* Maybe use [this](https://redis.io/commands/memory-usage/) to choose what sessions to offload
* Maybe use [this](https://github.com/kubernetes-sigs/metrics-server)

Optional TODO:

* Move `String EdgeInfrastructureUtils.getParentHost()` to Deployer jar. Should be an env variable created in `faas-cli deploy`.
* Completely hide internal serialization/deserialization type (json) from function calls (SessionToken.initJson())
* ~~Replace json.simple with Gson~~
* ~~Reduce number of redis replicas (just 1 read container)~~
* Refactor RedisHandler

## Exposed Edge API

### Extends

`Offloadable`: meant to replace `com.openfaas.model.AbstractHandler` to manage the offloading of a function. Instead of implementing `Handle` method, programmers have to implement `HandleOffload` method.

```java
public abstract class Offloadable extends com.openfaas.model.AbstractHandler {
    
    public IResponse HandleOffload (IRequest req) ;
}
```

### EdgeDB API

| Function    | Parameters                                                    | Return                                                  | Description                                                           |
|-------------|---------------------------------------------------------------|---------------------------------------------------------|-----------------------------------------------------------------------|
| `set`       | `String key` `String value`                                   |                                                         | Locally performs a set with the session                               |
| `get`       | `String key`                                                  | `String` Value associated to the key                    | Locally performs a get with the session                               |
| `addToList` | `String key` `String value`                                   |                                                         | Locally performs a set with the session                               |
| `getList`   | `String key`                                                  | `List<String>` Values associated to the key of the list | Locally performs a get with the session                               |
| `propagate` | `String value` `String levelsToPropagateTo` `String function` |                                                         | Forward the data to all the `receivePropagate` on the specified nodes |

## Internal Edge API

### Protocol Structures

* SessionToken:

```JSON
{
    "session": "session_id", 
    "proprietaryLocation": "location_id", 
    "currentLocation": "location_id",
    "function": "function_name"
}
```

| Field                     | Description                                                                        | Constant |
|---------------------------|------------------------------------------------------------------------------------|----------|
| `session`                 | Id of the session                                                                  | Y        |
| `proprietaryLocation`     | Location where this session was created                                            | Y        |
| `currentLocation`         | Location where the session is currently offloaded (where the function is executed) | N        |
| `function`                | Function associated to the session                                                 | Y        |

* SessionData:

```JSON
{
    "session_data":
    [
        {
        "key": "key1",
        "data": "data1"
        },
        {
        "key": "key2",
        "data": "data2"
        },
        ...
    ]
}
```

### Client

1. Client calls `http://<edge_node>/function/<function_name>`
2. If statusCode == 307:
   1. Redirect to header "Location" (Example: `http://<middle_node>/function/<function_name>`)
   2. Repeat until no more 307

> The X-session contains an arbitrary string
> 
> We assume the X-session header is always present and it is always valid
> 
> When an onload happens, the middle nodes redirect to the edge and permanently save the information (unless the session is offloaded again on the middle node)

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
| `migrate-session` | `session=<session_id>` | SessionPacket (JSON)   | All              | All                                 | Called on the nodes that have a session that is not theirs anymore                                                                                                                                                                                                                                                                            |

### Debug

| Function          | Parameters             | Return                            | Deployed on | Called by                           | Description                                                                                                                                                                                                                                                                                                                                   |
|-------------------|------------------------|-----------------------------------|-------------|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `test-function`   | `session=<session_id>` | `offloaded: yes (location_id)/no` | All         | All excepts root                    | Clients call it on leafs. Children call it on parents                                                                                                                                                                                                                                                                                         |
