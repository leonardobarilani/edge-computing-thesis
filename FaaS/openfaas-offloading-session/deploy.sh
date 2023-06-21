DEPLOY="java -jar $SCRIPTS_PATH/edge-deployer.jar deploy "

# ROOT
echo Deploying ROOT functions

$DEPLOY session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery country --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"
$DEPLOY session-offloading-manager-migrate-session $SCRIPTS_PATH/infrastructure.json --inEvery country --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"
$DEPLOY session-offloading-manager-garbage-collector $SCRIPTS_PATH/infrastructure.json --inEvery country --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"

# MIDDLE NODES
echo Deploying MIDDLE NODES functions

$DEPLOY session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"
$DEPLOY session-offloading-manager-migrate-session $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"
$DEPLOY session-offloading-manager-update-session $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"
$DEPLOY session-offloading-manager-garbage-collector $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"

# EDGE
echo Deploying EDGE functions

$DEPLOY session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"
$DEPLOY session-offloading-manager-migrate-session $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"
$DEPLOY session-offloading-manager-update-session $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"
$DEPLOY session-offloading-manager-garbage-collector $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"

kubectl --context k3d-p1 wait deployment session-offloading-manager session-offloading-manager-garbage-collector session-offloading-manager-migrate-session -n openfaas-fn --for condition=Available=True --timeout=180s
kubectl --context k3d-p3 wait deployment session-offloading-manager session-offloading-manager-garbage-collector session-offloading-manager-update-session session-offloading-manager-migrate-session -n openfaas-fn --for condition=Available=True --timeout=180s
kubectl --context k3d-p2 wait deployment session-offloading-manager session-offloading-manager-garbage-collector session-offloading-manager-update-session session-offloading-manager-migrate-session -n openfaas-fn --for condition=Available=True --timeout=180s
