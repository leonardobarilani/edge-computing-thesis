source $(dirname $BASH_SOURCE)/script-utils.sh

COMPLETE_CLEAN=$SCRIPT_PATH/complete-clean.txt
OFFLOADING_REJECT=$SCRIPT_PATH/offloading-reject.txt
OFFLOADING_NULL=$SCRIPT_PATH/offloading-null.txt
OFFLOADING_ACCEPT=$SCRIPT_PATH/offloading-accept.txt
DEFAULT_CONFIG=$SCRIPT_PATH/default-config.txt
SIMPLE_TEST=$SCRIPT_PATH/test.py
OFFLOAD_CART=$SCRIPT_PATH/test_offload_cart.py
OFFLOAD_BOTH=$SCRIPT_PATH/test_offload_both.py
STRESS=$SCRIPT_PATH/test_stress.py
OFFLOAD_STRESS=$SCRIPT_PATH/test_stress_offloading.py
UPDATE_ACCESS_TIMESTAMP_TEST=$SCRIPT_PATH/test_update_access_timestamp.py
CLIENT_CENTRIC_CONSISTENCY_TEST=$SCRIPT_PATH/test_client_centric_consistency.py
OFFLOAD_NEW_SESSION=$SCRIPT_PATH/test_offload_new_session.py


# ----------- BEGIN STRESS TEST -----------
# Load data
countdown "Loading data for STRESS (Requires 1 node)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing STRESS (Requires 1 node)"
python3 $STRESS || exit 1

echo End Stress Test
# ----------- END STRESS TEST -----------


