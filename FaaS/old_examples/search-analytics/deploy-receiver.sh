deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
}

#deploy search-analytics-data-receivers infrastructure.json --inEvery city --inAreas p1 --minReplicas 1
deploy search-analytics-data-receivers infrastructure.json --inEvery district --inAreas p1 --minReplicas 1