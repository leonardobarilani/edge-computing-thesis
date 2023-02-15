DEPLOY="java -jar $SCRIPTS_PATH/edge-deployer.jar deploy "

$DEPLOY shopping-cart $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1" --faas-cli "--env=TYPE=cart"
$DEPLOY shopping-cart $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1" --faas-cli "--env=TYPE=cart"

$DEPLOY products-counter $SCRIPTS_PATH/infrastructure.json --inEvery country --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1" --faas-cli "--env=TYPE=counter"
$DEPLOY products-counter $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1" --faas-cli "--env=TYPE=counter"
