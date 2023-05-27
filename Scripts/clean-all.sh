if [ "$EUID" -ne 0 ] ; then
  echo "Please run as root"
  exit
fi

# Clean docker
yes | docker system prune
docker volume prune -a

# Clean k3s' processes
/usr/local/bin/k3s-killall.sh

# Clean RAM and swap
echo 3 > /proc/sys/vm/drop_caches && swapoff -a && swapon -a && printf '\nRam-cache and Swap Cleared\n'

# Clean apt
apt autoremove
apt clean

# Clean journal logs (leave 100MB)
journalctl --vacuum-size=100M

read -p "About to delete the registry. Press Enter to continue, Ctrl+C to exit"
# Clean registry
rm -r /home/leo/.local/share/docker-io-registry/docker/registry/v2/blobs/sha256 && printf '\nRegistry cleaned\n'

# Restart docker service
systemctl restart docker
