DEPLOY="java -jar $SCRIPTS_PATH/edge-deployer.jar deploy "

# ROOT
echo Deploying ROOT functions

# MIDDLE NODES
echo Deploying MIDDLE NODES functions

$DEPLOY session-offloading-manager-trigger $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"

# EDGE
echo Deploying EDGE functions

$DEPLOY session-offloading-manager-trigger $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"

kubectl --context k3d-p3 wait deployment session-offloading-manager-trigger -n openfaas-fn --for condition=Available=True --timeout=180s
kubectl --context k3d-p2 wait deployment session-offloading-manager-trigger -n openfaas-fn --for condition=Available=True --timeout=180s
