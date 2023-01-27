
if [ $# -eq 0 ]
then
    echo One argument needed: profilename
    exit 1
fi

echo \[WARNING\] By continuing all the openfaas/openfaas-fn pods that are not in the Ready status in the k3d cluster \"$1\" will be deleted. Press any button to continue or Ctrl+C to exit the script
read

kubectl config use-context k3d-$1
kubectl get pod -A | grep -e Error -e Evicted -e ContainerStatusUnknown -e Completed -e Pending | awk '{print $2}' | xargs kubectl delete pod -n openfaas-fn
kubectl get pod -A | grep -e Error -e Evicted -e ContainerStatusUnknown -e Completed -e Pending | awk '{print $2}' | xargs kubectl delete pod -n openfaas
$SCRIPTS_PATH/install-openfaas.sh k3d-$1
$SCRIPTS_PATH/install-redis.sh k3d-$1

echo
echo Cluster, openfaas, redis redeployed
echo 
echo You can now fetch the new IP \(print-info k3d-\<node_name\>\) and update the infrastructure.json file



