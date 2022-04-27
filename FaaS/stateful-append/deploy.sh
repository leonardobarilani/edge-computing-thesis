deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
}

# ROOT
echo Deploying ROOT functions

#deploy stateful-append infrastructure.json --inEvery country --inAreas p1

# MIDDLE NODES
echo Deploying MIDDLE NODES functions

#deploy stateful-append infrastructure.json --inEvery city --inAreas p2

# EDGE
echo Deploying EDGE functions

deploy stateful-append infrastructure.json --inEvery district --inAreas p3
