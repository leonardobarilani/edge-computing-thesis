
if [ $# -eq 0 ]
then
    echo One argument needed: profilename
    exit 1
fi

echo \[WARNING\] By continuing all the k3d nodes will be delete. Press any button to continue or Ctrl+C to exit the script
read

k3d node delete --all
$SCRIPTS_PATH/install-cluster.sh $1
$SCRIPTS_PATH/install-openfaas.sh k3d-$1
$SCRIPTS_PATH/install-redis.sh k3d-$1

echo
echo Cluster, openfaas, redis redeployed
echo 
echo You can now fetch the new IP \(print-info\) and update the infrastructure.json file
