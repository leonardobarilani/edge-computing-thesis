deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
}

deploy store-crowdness infrastructure.json --inEvery city --inAreas p1 --receivePropagate --minReplicas 1
