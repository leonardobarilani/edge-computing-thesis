
if [ $# -eq 0 ]
then
    echo 'One argument needed (will be appended to "k3d-"): profilename'
    exit 1
fi

kubectl config use-context $1

k3d cluster create $1 \
    --network MyNet \
    --config $SCRIPTS_PATH/SimpleClusterConfig.yaml \
    --k3s-arg "--disable=traefik@server:0" \
    --verbose \
    --trace
# For extra options, visit:
# https://k3d.io/v5.4.4/usage/commands/k3d_cluster_create/
