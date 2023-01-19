#install kubectl dashboard
kubectl create clusterrolebinding kubernetes-dashboard --clusterrole=cluster-admin --serviceaccount=kubernetes-dashboard:kubernetes-dashboard -n kubernetes-dashboard
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
kubectl -n kubernetes-dashboard patch deploy kubernetes-dashboard --type='json' -p='[{"op": "add", "path": "/spec/template/spec/containers/0/args/-", "value": "--enable-skip-login"}]'
kubectl -n kubernetes-dashboard patch svc kubernetes-dashboard --type='json' -p '[{"op":"replace","path":"/spec/type","value":"NodePort"},{"op":"replace","path":"/spec/ports/0/nodePort","value":30040}]'
xdg-open https://"$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}")":30040/#/workloads?namespace=_all 1> /dev/null 2> /dev/null & disown