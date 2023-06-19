#!/bin/bash

if [[ $# -lt 2 ]]; then
    echo "This script requires two parameters."
    echo "Usage: $0 ROOT LEVEL"
    exit 1
fi

ROOT=$1
LEVEL=$2

DEPLOY="java -jar $SCRIPTS_PATH/edge-deployer.jar deploy "

$DEPLOY session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery $LEVEL --inAreas $ROOT --faas-cli "--label com.openfaas.scale.min=1" --faas-cli "--yaml ../stack.yml"
$DEPLOY session-offloading-manager-migrate-session $SCRIPTS_PATH/infrastructure.json --inEvery $LEVEL --inAreas $ROOT --faas-cli "--label com.openfaas.scale.min=1" --faas-cli "--yaml ../stack.yml"
$DEPLOY session-offloading-manager-update-session $SCRIPTS_PATH/infrastructure.json --inEvery $LEVEL --inAreas $ROOT --faas-cli "--label com.openfaas.scale.min=1" --faas-cli "--yaml ../stack.yml"

./load-curl-in-local-registry.sh

kubectl apply -f caller-offload-trigger.yaml
kubectl apply -f caller-garbage-collector.yaml

printf "\nIf you'd like to stop the offload trigger caller, execute this in the appropriate kubectl context: kubectl delete -f caller-offload-trigger.yaml\n"
printf "\nIf you'd like to stop the garbage collector caller, execute this in the appropriate kubectl context: kubectl delete -f caller-garbage-collector.yaml\n"
