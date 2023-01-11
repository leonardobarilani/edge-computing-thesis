k3d node delete --all
$HOME/thesis-scripts/install-cluster.sh p3
$HOME/thesis-scripts/install-openfaas.sh k3d-p3
$HOME/thesis-scripts/install-redis.sh k3d-p3

echo
echo Cluster, openfaas, redis redeployed
echo 
echo You can now fetch the new IP \(print-info\) and update the infrastructure.json file