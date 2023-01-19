# Creating a cluster

To install the local registry that will be used by the cluster to pull the docker images, run:

	install-registry

> Run `registry-list` to get a raw list of the docker images uploaded to the registry

To install the empty k3d cluster, run:

	install-cluster <cluster_name>

> Run `print-info <node_name>` to get the IP associated to the cluster (It will be needed to populate the `infrastructures.json` file)

> Run `install-k3d-dashboard` to open a web dashboard of the current node. Use `kubectl config use-context <node_name>` and `install-k3d-dashboard` again to open the dashboard of another node