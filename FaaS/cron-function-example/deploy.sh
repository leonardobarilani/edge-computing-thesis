DEPLOY="java -jar $SCRIPTS_PATH/edge-deployer.jar deploy "

$DEPLOY example $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"
