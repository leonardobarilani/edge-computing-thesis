#!/bin/sh
#todo make cron-connector optional
if [ $# -eq 0 ]
then
    echo One argument needed: profilename
    exit 1
fi

printf "\n"

kubectl --context $1 apply -f https://raw.githubusercontent.com/openfaas/faas-netes/master/namespaces.yml

helm upgrade openfaas --install openfaas/openfaas \
  --namespace openfaas \
  --set basic_auth=false \
  --set functionNamespace=openfaas-fn \
  --kube-context $1 \
  --debug

#helm upgrade --install cron-connector openfaas/cron-connector --namespace openfaas --set basic_auth=false --wait

# For extra options, visit:
# https://github.com/openfaas/faas-netes/blob/master/chart/openfaas/README.md#configuration
# now can do things like:
# kubectl -n openfaas-fn get functions
#Scale PODs up/down:
 #```bash
 #curl -d '{"serviceName":"KEK", "replicas": 1}' -X POST http://NODEIP:31112/system/scale-function/KEK
 #```
 #kubectl edit deployment/KEK -n openfaas-fn
