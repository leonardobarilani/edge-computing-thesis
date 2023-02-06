deploy()
{
	# shellcheck disable=SC2124
	# shellcheck disable=SC2027
	COMMAND="java -jar $SCRIPTS_PATH/edge-deployer.jar deploy "$@""
	echo $COMMAND
	$COMMAND
}

# ROOT
echo Deploying ROOT functions

deploy session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery country --inAreas k3d-p1

# MIDDLE NODES
echo Deploying MIDDLE NODES functions

deploy session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas k3d-p1

# EDGE
echo Deploying EDGE functions

deploy session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas k3d-p1

