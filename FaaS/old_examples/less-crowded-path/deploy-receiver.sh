deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
}

deploy video-footage-receiver infrastructure.json --inEvery district --inAreas p1 --minReplicas 1
