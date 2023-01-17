# TODO

## Deployer

* Replace System.out and System.err with Logger and implement verbosity parameter
* Run an openfaas "ping" before deploying even for unused nodes (silent if online)
* Change the check-infrastructures inside deploy command to be silent if there are no errors
* Remove redundant "infrastructure" in check-infrastructure and print-infrastructure
* Fix --inAreas bug (when not specified it uses the first location in the names' array instead of the name of the root location)
* Add the option --autofill-ip in the deployer. It calls minikube ip on every areaName in the json infrastructure and fills the openfaas_gateway with the received ip
* Add try/catch to handle when there are not required files (example: infrastructure.json is specified, but it does not exist)

## session-offloading-manager

* Guide on how to deploy everything (setup of containers, writing functions, calling functions)
* ~~Add parameter <session> to /force-offload to allow the Oracle to offload a specific session~~
* ~~Fix onload-session bug (example: A with children B and C. B offload to A. C call onload on A. A onload session of node B to node C, which is wrong)~~
* Implement iterative (parent-up-to-root) onload-session
* Maybe use [this](https://redis.io/commands/memory-usage/) to choose what sessions to offload
* Maybe use [this](https://github.com/kubernetes-sigs/metrics-server)

Optional TODOs:

* Embed Offloadable into openfaas:entrypoint
* Move `String EdgeInfrastructureUtils.getParentHost()` to Deployer jar. Should be an env variable created in `faas-cli deploy`.
* Completely hide internal serialization/deserialization type (json) from function calls (SessionToken.initJson())
* ~~Replace json.simple with Gson~~
* ~~Reduce number of redis replicas (just 1 read container)~~
* Refactor RedisHandler

