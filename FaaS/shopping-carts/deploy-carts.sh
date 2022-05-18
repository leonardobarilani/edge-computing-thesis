deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
}

deploy shopping-cart infrastructure.json --inEvery city --inAreas p1 --minReplicas 1
deploy shopping-cart infrastructure.json --inEvery district --inAreas p1 --minReplicas 1
