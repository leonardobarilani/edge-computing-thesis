#USERNAME=$(cat $HOME/docker-username.txt)
#PASSWORD=$(cat $HOME/docker-password.txt)

k3d registry create docker-io \
  --port 5000 \
  --volume ~/.local/share/docker-io-registry:/var/lib/registry
#  --proxy-remote-url https://registry-1.docker.io \
#  --proxy-username $USERNAME \
#  --proxy-password $PASSWORD \
