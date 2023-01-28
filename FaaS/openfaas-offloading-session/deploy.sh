deploy()
{
	COMMAND="java -jar $SCRIPTS_PATH/edge-deployer.jar deploy "$@""
	echo $COMMAND
	$COMMAND
}

# ROOT
echo Deploying ROOT functions

deploy session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery country --inAreas p1

# MIDDLE NODES
echo Deploying MIDDLE NODES functions

deploy session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery city --inAreas p1

# EDGE
echo Deploying EDGE functions

deploy session-offloading-manager $SCRIPTS_PATH/infrastructure.json --inEvery district --inAreas p1

