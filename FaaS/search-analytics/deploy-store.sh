deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
}

deploy search-analytics-store-data infrastructure.json --inEvery city --inAreas p1 --receivePropagate --minReplicas 1
