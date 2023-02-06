# Deploying the framework

## OpenFaaS and Redis

To deploy the framework on a node, run:

	install-openfaas k3d-<node_name>
	install-redis k3d-<node_name>

> To debug redis you can use `redis-client-cli` to launch a redis shell connected with the pod

> To see the status of the node in real time you can use `offloading-dashboard k3d-<node_name>`

When deploying a cluster with a previously used name, you can instead use `reinstall-all <node_name>`. This command will
delete the previous cluster and then run `install-cluster`, `install-openfaas` and `install-redis`

## offloading-session-manager

To deploy the offloading feature, run:

	./FaaS/openfaas-offloading-session/deploy.sh
