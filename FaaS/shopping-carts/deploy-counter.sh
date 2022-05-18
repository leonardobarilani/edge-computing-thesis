deploy()
{
	echo java -jar edge-deployer.jar deploy "$@"
	java -jar edge-deployer.jar deploy "$@"
}

deploy products-counter infrastructure.json --inEvery city --inAreas p1 --receivePropagate --minReplicas 1
