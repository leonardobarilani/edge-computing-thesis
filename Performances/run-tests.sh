source $(dirname $BASH_SOURCE)/script-utils.sh

IP_P3=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p3)
IP_P2=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p2)
IP_P1=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p1)

COMPLETE_CLEAN=$SCRIPT_PATH/complete-clean.txt
DEFAULT_CONFIG=$SCRIPT_PATH/default-config.txt

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

