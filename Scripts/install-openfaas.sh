#!/bin/sh

if [ $# -eq 0 ]
then
    echo One argument needed: profilename
    exit 1
fi

kubectl config use-context $1
printf "\n"

kubectl apply -f https://raw.githubusercontent.com/openfaas/faas-netes/master/namespaces.yml

helm upgrade openfaas --install openfaas/openfaas \
  --namespace openfaas \
  --set basic_auth=false \
  --set functionNamespace=openfaas-fn \
  --wait

helm upgrade --install cron-connector openfaas/cron-connector --namespace openfaas --set basic_auth=false --wait

# For extra options, visit:
# https://github.com/openfaas/faas-netes/blob/master/chart/openfaas/README.md#configuration
# now can do things like:
# kubectl -n openfaas-fn get functions
#Scale PODs up/down:
 #```bash
 #curl -d '{"serviceName":"KEK", "replicas": 1}' -X POST http://NODEIP:31112/system/scale-function/KEK
 #```
 #kubectl edit deployment/KEK -n openfaas-fn
