deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
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
