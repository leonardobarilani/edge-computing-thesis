k3d registry create docker-io.localhost \
  --port 5000 \
  --volume ~/.local/share/docker-io-registry:/var/lib/registry
