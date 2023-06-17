deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
}

deploy get-less-crowded-path infrastructure.json --inEvery city --inAreas p1 --minReplicas 1
