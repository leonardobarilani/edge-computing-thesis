#Install Docker to be executed without root:
read -p "Press any key to install docker ..."
echo 1
sudo apt-get install ca-certificates curl gnupg lsb-release
echo 2
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo 25
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
echo 3
sudo apt update
echo 4
sudo apt install docker-ce docker-ce-cli containerd.io docker-compose-plugin -y
sudo groupadd docker
echo 5
sudo usermod -aG docker $USER
echo 6
# newgrp docker
echo 7
docker run hello-world #check if working
echo 8
docker rm -f "$(docker ps -a -q)"
echo 9
docker rmi hello-world

#Install kubectl:
read -p "Press any key to install kubectl ..."
sudo apt install -y ca-certificates
curl -fsSL https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-archive-keyring.gpg
echo "deb [signed-by=/etc/apt/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt update
sudo apt install -y kubectl

#install k3d:
read -p "Press any key to install k3d ..."
curl -s https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | bash

#install helm
read -p "Press any key to install helm ..."
curl -sSLf https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash
helm repo add openfaas https://openfaas.github.io/faas-netes/
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

#install faas-cli
curl -sSL https://cli.openfaas.com | sudo sh

#install SCRIPTS_PATH and aliases in .bashrc
SHELLRC=$HOME/.bashrc
source $SHELLRC
if [[ -z "${SCRIPTS_PATH}" ]]; then
	SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
	echo "">> $SHELLRC
	echo "">> $SHELLRC
	echo "# Pointer to edge-computing-thesis scripts">> $SHELLRC
	echo "export SCRIPTS_PATH='$SCRIPT_DIR'">> $SHELLRC
	echo "source \$SCRIPTS_PATH/aliases.sh">> $SHELLRC
	echo "">> $SHELLRC
fi

#install autocompletion
echo \[WARNING\] If you want to install autocompletion \(kubectl, k3d, helm\) scripts in /etc/bash_completion.d folder, press any button, else Ctrl+C to exit the script
read
apt-get install bash-completion -y
sudo curl https://raw.githubusercontent.com/docker/cli/master/contrib/completion/bash/docker -o /etc/bash_completion.d/docker
kubectl completion bash | sudo tee /etc/bash_completion.d/kubectl > /dev/null #optional
k3d completion bash | sudo tee /etc/bash_completion.d/k3d > /dev/null #optional
helm completion bash | sudo tee /etc/bash_completion.d/helm > /dev/null #optional
faas-cli completion --shell bash | sudo tee /etc/bash_completion.d/faas-cli > /dev/null #optional
