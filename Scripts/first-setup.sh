#Install Docker to be executed without root:
read -p "Press any key to install docker ...\n"
curl -sS https://get.docker.com | sh
sudo groupadd docker
sudo usermod -aG docker $USER
newgrp docker
docker run hello-world #check if working
docker rm -f "$(docker ps -a -q)"
docker rmi hello-world

#Install kubectl:
read -p "Press any key to install kubectl ...\n"
sudo apt install -y ca-certificates
sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg
echo "deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt update
sudo apt install -y kubectl

#install k3d:
read -p "Press any key to install k3d ...\n"
curl -s https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | bash

#install helm
read -p "Press any key to install helm ...\n"
curl -sSLf https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash
helm repo add openfaas https://openfaas.github.io/faas-netes/
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

#install SCRIPTS_PATH and aliases in .bashrc
if [[ -z "${SCRIPTS_PATH}" ]]; then
	SHELLRC=$HOME/.bashrc
	read -p "The env variable SCRIPTS_PATH is not defined. It will be place in $(echo -n ). Please specify the full absolute path that contains the scripts \(example: /foo/bar/edge-computing-thesis/Scripts\): " NEW_SCRIPTS_PATH
	echo "\n\n# Pointer to edge-computing-thesis scripts">> SHELLRC
	echo "export SCRIPTS_PATH='$($NEW_SCRIPTS_PATH)'">> SHELLRC
	echo "source \$SCRIPTS_PATH/aliases.sh\n">> SHELLRC
fi

#install autocompletion
echo \[WARNING\] If you want to install autocompletion \(kubectl, k3d, helm\) scripts in /etc/bash_completion.d folder, press any button, else Ctrl+C to exit the script
read
apt-get install bash-completion -y
sudo curl https://raw.githubusercontent.com/docker/cli/master/contrib/completion/bash/docker -o /etc/bash_completion.d/docker
kubectl completion bash | sudo tee /etc/bash_completion.d/kubectl > /dev/null #optional
k3d completion bash | sudo tee /etc/bash_completion.d/k3d > /dev/null #optional
helm completion bash | sudo tee /etc/bash_completion.d/helm > /dev/null #optional
