
if [ $# -eq 0 ]
then
	echo No arguments given: printing all the context:
	for context in $(kubectl config get-contexts | awk '{print $2}' | tail -n +2);
	do
		echo
		echo Entry point for $context:
		echo http://"$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context $context)":31112
		echo
	done
else
	echo
	echo Entry point for $1:
	echo http://"$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context $1)":31112
	export OPENFAAS_URL=http://$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"):31112
	echo
fi


