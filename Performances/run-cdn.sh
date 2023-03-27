source $(dirname $BASH_SOURCE)/script-utils.sh

IP_P3=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p3)
IP_P2=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p2)
IP_P1=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p1)

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

echo Test simple
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 cdn.py \
	--requests 10 \
	--clients 1 \
	--offloaded 0 \
	--sessions 1 \
	--filespersession 1 \
	--kbperfile 5
echo End simple test

echo Test edge only
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 cdn.py \
	--requests 10 \
	--clients 100 \
	--offloaded 0 \
	--sessions 10 \
	--filespersession 10 \
	--kbperfile 1024
echo End edge only test

echo Test 50\% offload
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 cdn.py \
	--requests 10 \
	--clients 100 \
	--offloaded .5 \
	--sessions 10 \
	--filespersession 10 \
	--kbperfile 1024
echo End 50\% offload test