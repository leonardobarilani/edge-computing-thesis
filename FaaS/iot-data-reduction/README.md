# IoT Data Reduction Use Case

## Topology

1. Cloud
2. Building

## Functions

| Name                  | Type              | Where     | Callers                                               |
|-----------------------|-------------------|-----------|-------------------------------------------------------|
| `iot-data-reducer`    | Offloadable       | Building  | Called by IoT devices to update the devices' value    |
| `iot-data-receiver`   | ReceivePropagate  | Cloud     | Called by `iot-data-reducer` through `propagate()`    |

* `iot-data-reducer` is called by IoT devices that send the current value of the device. If the value has changed, then it gets propagated to `iot-data-receiver`
* `iot-data-receiver` just receive the new updated values of the IoT sensor and store it

## Database

* `iot-data-reducer` uses sessions to store the last value. The session represent the device that sent the value. Sessions doesn't expire
* `iot-data-receiver` uses the local database to store the last value sent by the reducer. Doesn't expire
