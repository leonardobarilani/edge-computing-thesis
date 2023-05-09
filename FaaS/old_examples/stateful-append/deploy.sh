deploy()
{
	echo java -jar $HOME/thesis-scripts/edge-deployer.jar deploy "$@"
	java -jar $HOME/thesis-scripts/edge-deployer.jar deploy "$@"
}

IP=$(kubectl config use-context k3d-p3 > /dev/null && kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}")
faas-cli remove stateful-append --gateway http://$IP:31112
sleep 5
deploy stateful-append infrastructure.json --inEvery district --inAreas p3
