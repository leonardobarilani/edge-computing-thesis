source $(dirname $BASH_SOURCE)/script-utils.sh

P3_IP=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p3)
P2_IP='192.168.1.3'

P2_SSH='giampietrofb@192.168.1.3'
P2_CLEAN_NODE='/home/giampietrofb/OneDrive/tesi/edge-computing-thesis/Performances/clean-node.sh'

COMPLETE_CLEAN=$SCRIPT_PATH/complete-clean.txt
DEFAULT_CONFIG=$SCRIPT_PATH/default-config.txt
SESSION_PREAMBLE=$SCRIPT_PATH/session-preamble.txt

function load_session_of_kbytes ()
{
	execute_redis_commands $SESSION_PREAMBLE
	printf "select 2\nhset marco key $(dd if=/dev/zero bs=1024 count=$1 | tr '\0' '\141')\n" | kubectl exec -n openfaas-fn -it my-openfaas-redis-master-0 -- redis-cli
}

function now ()
{
	date --utc +%Y-%m-%dT%TZ
}

echo Test average_response_time
ssh $P2_SSH $P2_CLEAN_NODE
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_response_time.py --count 100 --url http://$P2_IP:31112/function/empty-function