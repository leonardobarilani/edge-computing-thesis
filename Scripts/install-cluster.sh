
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
    #uncomment this line to expose the internal port 31112 (openfaas-gateway) to the external 31112 port
    #if you have multiple cluster on the same computer, remember to expose each cluster on a different PORT (--port "PORT:31112@server:0)
    # --port "31112:31112@server:0"
# For extra options, visit:
# https://k3d.io/v5.4.4/usage/commands/k3d_cluster_create/
