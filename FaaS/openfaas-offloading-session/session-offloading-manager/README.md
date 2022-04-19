# Offloading draft

TODO:

* ~~Change `set-offload-status` from `status=<yes\no>` to `status=<accept/reject>`.~~
* ~~Change unload to onload~~
* ~~Change receive-session to offload-session~~
* Move `String EdgeInfrastructureUtils.getParentHost()` to Deployer jar. Should be an env variable created in `faas-cli deploy`.

## Commons

* SessionToken:

```JSON
{
    "session":"id_of_the_session", 
    "proprietaryLocation":"location_id", 
    "currentLocation":"location_id"
}
```

* RedisHandler
* EdgeInfrastructure/HTTP Utils (+ Response)

## Functions

### Client API

| Function         | Parameters             | Return       | Deployed on | Called by | Description                           |
|------------------|------------------------|--------------|-------------|-----------|---------------------------------------|
| `create-session` |                        | `session_id` | Leaf        | Clients   | Gives a client a unique session token |
| `delete-session` | `session=<session_id>` |              | All         | Clients   | Delete a session token                |

### Oracle API

| Function             | Parameters               | Return | Deployed on     | Called by       | Description                                                                                                                          |
|----------------------|--------------------------|--------|-----------------|-----------------|--------------------------------------------------------------------------------------------------------------------------------------|
| `set-offload-status` | `status=<accept\reject>` |        | All except root | Metrics oracle  | Arbitrarily set the offloading status of the local node. `reject` means that the node will forward the offloading to the parent node |
| `force-offload`      |                          |        | All except root | Metrics oracle  | The local node has to offload a random function                                                                                      |
| `force-onload`       |                          |        | All except root | Metrics oracle  | The local node has to call his parent's `onload-session`                                                                             |

### Offloading API

| Function          | Parameters             | Return                            | Deployed on      | Called by                           | Description                                                                                                                                                                                                                                                                                                                                   |
|-------------------|------------------------|-----------------------------------|------------------|-------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `offload-session` | JSON of session object |                                   | All except leafs | Nodes that are offloading a session | Called to offload a session. The receiving node can decide if offload the session also or accept it based on the current offloading status. If the local node actually accept the offloading it has to update the session object and perform the migration from `currentLocation` to itself and update `proprietaryLocation`'s session object |                                                                                                                                                                                                                                                                                     |
| `onload-session`  |                        | JSON of session object            | All except leafs | All except root                     | Children call it on parent to unload sessions, if the parent has some sessions coming from the child subtree. Returns an arbitrary number of sessions objects                                                                                                                                                                                 |
| `test-function`   | `session=<session_id>` | `offloaded: yes (location_id)/no` | All              | All excepts root                    | Clients call it on leafs. Children call it on parents                                                                                                                                                                                                                                                                                         |
| `update-session`  | JSON of session object |                                   | Leaf             | All except leafs                    | The local node has just accepted an offload, so it has to update the session object of the leaf                                                                                                                                                                                                                                               |
