deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
}

deploy search-analytics-performer infrastructure.json --inEvery city --inAreas p1 --minReplicas 1
