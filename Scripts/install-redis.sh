
if [ $# -eq 0 ]
then
	echo One argument needed: profilename
	exit 1
fi

kubectl config use-context $1
printf "\n"

helm upgrade --install my-openfaas-redis \
	--namespace openfaas-fn \
	--set auth.enabled=false \
	--set architecture=standalone \
	--set persistence.enabled=false \
	--set master.disableCommands=null \
	--wait \
	bitnami/redis 
# For extra options, visit:
# https://github.com/bitnami/charts/tree/main/bitnami/redis#parameters
