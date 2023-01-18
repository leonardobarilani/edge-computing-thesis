# Scripts

This repository contains some scripts used to conveniently debug and develop the edge computing thesis

## Shell Prerequisites

These scripts assume that the shell has the following commands installed:

* `kubectl`
* `k3d`
* `helm`
* `docker`
* `curl`
* `tmux` (Only for thesis-dashboard)

You can use first_setup.sh to install them all

Add the following snippet in your `.bashrc` or equivalent to quickly access the scripts and to create the `SCRIPTS_PATH` variable:

```sh
export SCRIPTS_PATH='/home/leo/edge-computing-thesis/Scripts' # Change this to the folder where you saved the scripts

alias thesis-dashboard='${SCRIPTS_PATH}/thesis-dashboard.sh'
alias print-info='${SCRIPTS_PATH}/print-info.sh'
alias install-registry='${SCRIPTS_PATH}/install-registry.sh'
alias install-cluster='${SCRIPTS_PATH}/install-cluster.sh'
alias install-openfaas='${SCRIPTS_PATH}/install-openfaas.sh'
alias install-redis='${SCRIPTS_PATH}/install-redis.sh'
alias edge-deployer='java -jar ${SCRIPTS_PATH}/edge-deployer'
alias print-events='kubectl get events --all-namespaces  --sort-by='.metadata.creationTimestamp''
alias registry-list='${SCRIPTS_PATH}/registry-list.sh'
alias reinstall-all='${SCRIPTS_PATH}/reinstall-all.sh'
```

## Registry install

To install the local registry: `install-registry`.

To push an image to the local registry: `local-push new_image_name new_version`.

> `new_image_name` should be in the format `user_name/image_name`. The `user_name` is not tied to any kind of login mechanisms.

## Cluster install

To install a new cluster: `install-cluster new_cluster_name`.

> After the installation is successful, refer to the new cluster with `k3d-new_cluster_name` when using `kubectl` and all the other scripts.

After cluster installation, you can install OpenFaaS and Redis:

```sh
install-openfaas cluster_name
install-redis cluster_name
```

To install session-offloading-manager, move into the folder `FaaS/openfaas-offloading-session/` and run:

```sh
./up.sh
```

