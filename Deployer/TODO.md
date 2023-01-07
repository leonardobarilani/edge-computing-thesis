# TODO

* Replace System.out and System.err with Logger and implement verbosity parameter
* Run an openfaas "ping" before deploying even for unused nodes (silent if online)
* Change the check-infrastructures inside deploy command to be silent if there are no errors
* Remove redundant "infrastructure" in check-infrastructure and print-infrastructure
* Fix --inAreas bug (when not specified it uses the first location in the names' array instead of the name of the root location)
* Add the option --autofill-ip in the deployer. It calls minikube ip on every areaName in the json infrastructure and fills the openfaas_gateway with the received ip
* Add try/catch to handle when there are not required files (example: infrastructure.json is specified, but it does not exist)
