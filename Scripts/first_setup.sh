#Install Docker to be executed without root:
read -p "Press any key to install docker ..."
curl -sS https://get.docker.com | sh
sudo groupadd docker
sudo usermod -aG docker $USER
newgrp docker
docker run hello-world #check if working
docker rm -f "$(docker ps -a -q)"
docker rmi hello-world

#Install kubectl:
read -p "Press any key to install kubectl ..."
sudo apt install -y ca-certificates
sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg
echo "deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt update
sudo apt install -y kubectl

#install k3d:
read -p "Press any key to install k3d ..."
curl -s https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | bash

#install helm
read -p "Press any key to install helm ..."
curl -sSLf https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 | bash

echo \[WARNING\] If you want to install autocompletion scripts in /etc/bash_completion.d folder, press any button, else Ctrl+C to exit the script
read
apt-get install bash-completion -y
sudo curl https://raw.githubusercontent.com/docker/cli/master/contrib/completion/bash/docker -o /etc/bash_completion.d/docker
kubectl completion bash | sudo tee /etc/bash_completion.d/kubectl > /dev/null #optional
k3d completion bash | sudo tee /etc/bash_completion.d/k3d > /dev/null #optional
helm completion bash | sudo tee /etc/bash_completion.d/helm > /dev/null #optional
