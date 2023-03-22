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

if false; then
echo Test average_response_time
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_response_time.py --count 100 --url http://$IP_P3:31112/function/empty-function

echo Test average_response_time_redirect
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_response_time_redirect.py --count 100

echo Test average_offload_time with 1KB
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
load_session_of_kbytes $((1))
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_offload_time.py --count 2 --tests 100 --filename results/$(now)average_offload_time_1kb.png

echo Test average_offload_time with 512KB
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
load_session_of_kbytes $((512))
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_offload_time.py --count 2 --tests 100 --filename results/$(now)average_offload_time_512kb.png

echo Test average_offload_time with 1MB
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
load_session_of_kbytes $((1024))
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_offload_time.py --count 2 --tests 100 --filename results/$(now)average_offload_time_1mb.png

echo Test average_offload_time with 5MB
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
load_session_of_kbytes $((1024 * 5))
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_offload_time.py --count 2 --tests 100 --filename results/$(now)average_offload_time_5mb.png

echo Test average_offload_time with 10MB
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
load_session_of_kbytes $((1024 * 10))
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_offload_time.py --count 2 --tests 100 --filename results/$(now)average_offload_time_10mb.png

echo Test average_offload_time with 50MB
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
load_session_of_kbytes $((1024 * 50))
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_offload_time.py --count 2 --tests 100 --filename results/$(now)average_offload_time_50mb.png

fi

echo Test average_offload_time with 100MB
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
load_session_of_kbytes $((1024 * 100))
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
python3 average_offload_time.py --count 2 --tests 100 --filename results/$(now)average_offload_time_100mb.png
