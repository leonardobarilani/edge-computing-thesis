#!/bin/bash
# Function to perform a task
perform_task() {
  cluster=$1
  echo "Installing cluster $cluster"
  $SCRIPTS_PATH/install-k3d-cluster.sh "$cluster"
  kubectl --context "k3d-$cluster" wait deployment -n kube-system metrics-server local-path-provisioner coredns --for condition=Available=True --timeout=-1s
  $SCRIPTS_PATH/install-openfaas.sh "k3d-$cluster"
  kubectl --context "k3d-$cluster" wait deployment -n openfaas alertmanager queue-worker prometheus nats gateway --for condition=Available=True --timeout=-1s
  $SCRIPTS_PATH/install-redis.sh "k3d-$cluster"
  kubectl --context "k3d-$cluster" wait pods -n openfaas-fn my-openfaas-redis-master-0 --for condition=Available=True --timeout=-1s

  echo "Cluster $cluster installation completed"
}

k3d cluster delete p1 p2 p3
helm repo update

# Start tasks for each cluster in the background
perform_task p1 &
#perform_task p2 &
#perform_task p3 &

# Wait for all background tasks to complete
wait

echo
echo Cluster, openfaas, redis redeployed
echo
$SCRIPTS_PATH/print-node-info.sh
