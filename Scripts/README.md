# Scripts

This repository contains some scripts used to conveniently debug and develop the edge computing thesis

## Prerequisites

These scripts assumes that the shell has the following commands installed:

* `kubectl`
* `k3d`
* `helm`
* `docker`
* `curl`
* `tmux` (Only for thesis-dashboard)

## Shell autocomplete

To activate shell autocomplete, you can add the following snippet in your `.bashrc` or equivalent:

```sh
SCRIPTS_PATH='~/thesis-scripts/'

alias thesis-dashboard='~/thesis-scripts/thesis-dashboard.sh'
alias print-info='~/thesis-scripts/print-info.sh'
alias install-registry='~/thesis-scripts/install-registry.sh'
alias install-cluster='~/thesis-scripts/install-cluster.sh'
alias install-openfaas='~/thesis-scripts/install-openfaas.sh'
alias install-redis='~/thesis-scripts/install-redis.sh'
alias edge-deployer='java -jar ~/thesis-scripts/edge-deployer'
alias print-events='kubectl get events --all-namespaces  --sort-by='.metadata.creationTimestamp''
alias registry-list='~/thesis-scripts/registry-list.sh'
alias local-push='~/thesis-scripts/local-push.sh'
alias reinstall-all='~/thesis-scripts/reinstall-all.sh'
```

### Registry usage

To install the local registry: `install-registry`.

To push an image to the local registry: `local-push new_image_name new_version`.

> `new_image_name` should be in the format `user_name/image_name`. The `user_name` is not tied to any kind login mechanisms.

### Cluster usage

To install a new cluster: `install-cluster new_cluster_name`.

> After the installation is successful, refer to the new cluster when using `kubectl` with `k3d-new_cluster_name`.

