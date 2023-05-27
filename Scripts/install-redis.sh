#todo set persistance as optional
#todo isn't simple deployment lighter than the helm one?
#todo test that disableCommands actually work to enable flushdb
if [ $# -eq 0 ]
then
	echo One argument needed: profilename
	exit 1
fi

printf "\n"

helm upgrade --install my-openfaas-redis \
	--namespace openfaas-fn \
	--set auth.enabled=false \
	--set architecture=standalone \
	--set persistence.enabled=false \
	--set master.disableCommands=null \
	--wait \
	--debug \
	--set master.service.type=LoadBalancer \
	--kube-context $1 \
	bitnami/redis
# For extra options, visit:
# https://github.com/bitnami/charts/tree/main/bitnami/redis#parameters
