
if [ $# -eq 0 ]
then
	echo One argument needed: profilename
	exit 1
fi

kubectl config use-context $1

# kubectl logs -n openfaas-fn -f --selector namespace=openfaas-fn
# kubectl logs -n openfaas-fn deploy/session-offloading-manager -f

echo ---------- Logs for context of $1 ----------

for i in $(kubectl get pods -n openfaas-fn | cut -d" " -f1); do kubectl logs $i -n openfaas-fn; done
