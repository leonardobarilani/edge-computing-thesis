deploy()
{
	COMMAND="java -jar $HOME/thesis-scripts/edge-deployer.jar deploy "$@""
	echo $COMMAND
	$COMMAND
}

# ROOT
echo Deploying ROOT functions

#deploy session-offloading-manager infrastructure.json --inEvery country --inAreas p1

# MIDDLE NODES
echo Deploying MIDDLE NODES functions

#deploy session-offloading-manager infrastructure.json --inEvery city --inAreas p2

# EDGE
echo Deploying EDGE functions

deploy session-offloading-manager infrastructure.json --inEvery district --inAreas p3

