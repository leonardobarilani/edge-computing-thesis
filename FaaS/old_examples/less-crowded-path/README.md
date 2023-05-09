# Less Crowded Path Use Case

## Topology

1. City
2. Cameras

## Functions

| Name                     | Type             | Where   | Callers                                                  |
|--------------------------|------------------|---------|----------------------------------------------------------|
| `video-footage-receiver` |                  | Cameras | Called by cameras to update the crowds' status           |
| `store-crowdness`        | ReceivePropagate | City    | Called by `video-footage-receiver` through `propagate()` |
| `get-less-crowded-path`  |                  | City    | Called by users to get the less crowded path             |

* `video-footage-receiver` analyse the image sent by the camera (in the demo we don't send images, but just a number
  that represent crowdness). Once the analysis is done, the crowdness level is propagated to the `store-crowdness` if
  there is a change in the crowdness level
* `store-crowdness` just stores the crowdness level in the upper locations
* `get-less-crowded-path` fetch the crowdness of locations from the storage and calculate the shortest path given a
  start and an end point as parameters

> TODO: the only thing that should be offloadable is get-less-crowded-path, not because of the used memory but for the
> path computations

## Database

* `video-footage-receiver` uses the database only to call `propagate()`
* `store-crowdness` access local database "crowdness" to store the crowdness of cameras. Doesn't expire
* `get-less-crowded-path` access local database "crowdness" to read the crowdness of cameras
