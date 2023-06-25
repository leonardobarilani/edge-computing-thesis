DEPLOY="java -jar $SCRIPTS_PATH/edge-deployer.jar deploy "

# ROOT
echo Deploying ROOT functions

# MIDDLE NODES
echo Deploying MIDDLE NODES functions

$DEPLOY speech-to-text $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"

# EDGE
echo Deploying EDGE functions

$DEPLOY speech-to-text $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1 --faas-cli "--label com.openfaas.scale.min=1"

