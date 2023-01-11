
if [ $# -eq 0 ]
then
    echo 'One argument needed (will be appended to "k3d-"): profilename'
    echo 'Optional argument: external port (default: 8081)'
    exit 1
fi

PORT=8081

if [ $# -eq 2 ]
then
    PORT=$2
fi

kubectl config use-context $1

k3d cluster create $1 \
    --registry-use k3d-docker-io:5000 \
    --registry-config $SCRIPTS_PATH/registry.yaml
#    -p "$PORT:80@loadbalancer" \

#it proxies requests to docker hub and redirects them to local-reg
#k3d kubeconfig merge $1 \
#    --kubeconfig-merge-default \
#    --kubeconfig-switch-context
