#todo use export instead of alias, and sobstitute - with _
#todo wouldn't it be better to just export this directory to the path?
alias offloading-dashboard='${SCRIPTS_PATH}/install-tmux-watchpanel.sh'
alias print-info='${SCRIPTS_PATH}/print-node-info.sh'
alias install-registry='${SCRIPTS_PATH}/install-registry.sh'
alias install-cluster='${SCRIPTS_PATH}/install-k3d-cluster.sh'
alias install-openfaas='${SCRIPTS_PATH}/install-openfaas.sh'
alias install-redis='${SCRIPTS_PATH}/install-redis.sh'
alias edge-deployer='java -jar ${SCRIPTS_PATH}/edge-deployer'
alias print-events='kubectl get events --all-namespaces  --sort-by='.metadata.creationTimestamp''
alias registry-list='${SCRIPTS_PATH}/print-k3d-registry-list.sh'
alias reinstall-cluster='${SCRIPTS_PATH}/setup-clusters.sh'
alias restore-cluster='${SCRIPTS_PATH}/restore-cluster.sh'
alias install-k3d-dashboard='${SCRIPTS_PATH}/install-k3d-dashboard.sh'
alias redis-client-cli='${SCRIPTS_PATH}/start-redis-client.sh'
alias watch-clusters='${SCRIPTS_PATH}/print-current-pods.sh'
