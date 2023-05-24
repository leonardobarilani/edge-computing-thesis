DEPLOY="java -jar $SCRIPTS_PATH/edge-deployer.jar deploy "

$DEPLOY empty-function $SCRIPTS_PATH/infrastructure.json --inEvery country --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=2"
$DEPLOY empty-function $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=2"
$DEPLOY empty-function $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=2"

