
if [ $# -eq 0 ]
then
    echo 'One argument needed (will be appended to "k3d-"): profilename'
    exit 1
fi

kubectl config use-context $1

k3d cluster create $1 \
    --k3s-arg "--disable=traefik@server:0" \
    --network MyNet \
    --config $SCRIPTS_PATH/SimpleClusterConfig.yaml \
    --verbose \
    --trace

#k3d kubeconfig merge $1 \
#    --kubeconfig-merge-default \
#    --kubeconfig-switch-context
# export OPENFAAS_URL=http://$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"):31112
