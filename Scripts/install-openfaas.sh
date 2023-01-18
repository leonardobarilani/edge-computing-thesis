#!/bin/sh

if [ $# -eq 0 ]
then
    echo One argument needed: profilename
    exit 1
fi

kubectl config use-context $1
printf "\n"

kubectl apply -f https://raw.githubusercontent.com/openfaas/faas-netes/master/namespaces.yml

#kubectl create secret generic basic-auth \
#  --from-literal=basic-auth-user=admin \
#  --from-literal=basic-auth-password=password \
#  -n openfaas 
helm upgrade openfaas --install openfaas/openfaas \
  --namespace openfaas \
  --set basic_auth=false \
  --set faasnetes.imagePullPolicy=IfNotPresent \
  --set functionNamespace=openfaas-fn \
  --wait
#  --set operator.create=true \
#  --set ingress.enabled=true \
#  --set ingress.annotations.kubernetes.io/ingress.class=traefik \
#  --set gateway.directFunctions=true

#USERNAME=$(cat $HOME/docker-username.txt)
#PASSWORD=$(cat $HOME/docker-password.txt)
#EMAIL=$(cat $HOME/docker-email.txt)

#kubectl create secret docker-registry my-private-repo \
#  --docker-username=$USERNAME \
#  --docker-password=$PASSWORD \
#  --docker-email=$EMAIL \
#  --docker-server=https://k3d-docker-io:5000/ \
#  --namespace openfaas-fn

  