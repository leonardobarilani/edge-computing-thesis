
if [ $# -eq 0 ]
then
    echo One argument needed: profilename
    exit 1
fi

echo \[WARNING\] By continuing the k3d cluster \"$1\" will be deleted, if it exists. Press any button to continue or Ctrl+C to exit the script
read

k3d cluster delete $1
$SCRIPTS_PATH/install-cluster.sh $1
kubectl wait deployment -n kube-system metrics-server local-path-provisioner coredns --for condition=Available=True --timeout=180s
$SCRIPTS_PATH/install-openfaas.sh k3d-$1
$SCRIPTS_PATH/install-redis.sh k3d-$1

echo
echo Cluster, openfaas, redis redeployed
echo
$SCRIPTS_PATH/print-info.sh k3d-$1
