
if [ $# -eq 0 ]
then
    echo One argument needed: profilename
    exit 1
fi

echo \[WARNING\] By continuing the k3d cluster \"$1\" will be deleted, if it exists. Press any button to continue or Ctrl+C to exit the script
read

k3d cluster delete $1
$SCRIPTS_PATH/install-cluster.sh $1
echo Wait for all pods to be ready and then press Enter...
read
$SCRIPTS_PATH/install-openfaas.sh k3d-$1
echo Wait for all pods to be ready and then press Enter...
read
$SCRIPTS_PATH/install-redis.sh k3d-$1

echo
echo Cluster, openfaas, redis redeployed
echo 
echo You can now fetch the new IP \(print-info k3d-\<node_name\>\) and update the infrastructure.json file
