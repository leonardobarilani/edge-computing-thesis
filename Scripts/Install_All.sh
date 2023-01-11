#Install Docker:
read -p "Press any key to install docker ..."
curl -sS https://get.docker.com | sh
sudo groupadd docker
sudo usermod -aG docker $USER
newgrp docker
docker run hello-world #check if working
docker rm -f $(docker ps -a -q)
docker rmi hello-world

#Install kubectl:
read -p "Press any key to install kubectl ..."
sudo apt install -y ca-certificates
sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg
echo "deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt update
sudo apt install -y kubectl
kubectl completion bash | sudo tee /etc/bash_completion.d/kubectl > /dev/null #optional


#install k3d:
read -p "Press any key to install k3d ..."
curl -s https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | bash
k3d completion bash | sudo tee /etc/bash_completion.d/kubectl > /dev/null #optional

#start k3d node with local registry
k3d registry create docker-io \
  -p 5000 \
  --proxy-remote-url https://registry-1.docker.io \
  -v ~/.local/share/docker-io-registry:/var/lib/registry
k3d cluster create desktop -p "8081:80@loadbalancer" --registry-use k3d-docker-io:5000 --registry-config registry.yaml
#it proxies requests to docker hub and redirects them to local-reg
k3d kubeconfig merge desktop --kubeconfig-merge-default --kubeconfig-switch-context

#install kubectl dashboard
read -p "Press any key to install kubectl dashboard ..."
kubectl create clusterrolebinding kubernetes-dashboard --clusterrole=cluster-admin --serviceaccount=kubernetes-dashboard:kubernetes-dashboard -n kubernetes-dashboard
kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
kubectl -n kubernetes-dashboard patch deploy kubernetes-dashboard --type='json' -p='[{"op": "add", "path": "/spec/template/spec/containers/0/args/-", "value": "--enable-skip-login"}]'
#kubectl proxy
#http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/

#get cluster ip
kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"
echo "export OPENFAAS_URL=http://$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"):31112" | sudo tee /etc/profile.d/faas-gateway.sh > /dev/null

#install helm
read -p "Press any key to install helm ..."
curl -sSLf https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash

#install faas-cli
curl -sSL https://cli.openfaas.com | sudo -E sh

#install openfaas
read -p "Press any key to deploy openfaas ..."
kubectl apply -f https://raw.githubusercontent.com/openfaas/faas-netes/master/namespaces.yml
helm repo add openfaas https://openfaas.github.io/faas-netes/
helm repo update
helm upgrade openfaas --install openfaas/openfaas \
  --namespace openfaas \
#  --set operator.create=true \
  --set basic_auth=false \
  --set faasnetes.imagePullPolicy=IfNotPresent \
  --set functionNamespace=openfaas-fn \
#  --set ingress.enabled=true \
#  --set ingress.annotations.kubernetes.io/ingress.class=traefik \
#  --set gateway.directFunctions=true \
  --wait
  
  #direct functions fa si che il gateway lasci a kubernetes trovare l ip dei servizi e fare load balancing
  #necessario quando si usano service mesh like Linkerd or Istio
    
#* To use NodePorts (default) pass no additional flags kubectl port-forward
#* To use a LoadBalancer add `--set serviceType=LoadBalancer`
#* To use an IngressController add `--set ingress.enabled=true` (ma di default ti setta nginx... con arkade posso specificare altro)
kubectl get svc -n openfaas gateway-external -o wide
kubectl get functions


