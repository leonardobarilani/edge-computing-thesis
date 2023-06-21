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

# ----------- BEGIN SIMPLE TEST -----------
# Load data
countdown "Loading data for SIMPLE_TEST (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing SIMPLE_TEST (Requires 2 nodes)"
python3 $SIMPLE_TEST || exit 1

echo End Simple Test
# ----------- END SIMPLE TEST -----------

# ----------- BEGIN UPDATE ACCESS TIMESTAMP TEST -----------
# Load data
countdown "Loading data for UPDATE_ACCESS_TIMESTAMP_TEST (Requires 1 node)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing UPDATE_ACCESS_TIMESTAMP_TEST (Requires 1 node)"
python3 $UPDATE_ACCESS_TIMESTAMP_TEST || exit 1

echo End Update Access Timestamp Test
# ----------- END UPDATE ACCESS TIMESTAMP TEST -----------

# ----------- BEGIN CLIENT CENTRIC CONSISTENCY TEST -----------
# Load data
countdown "Loading data for CLIENT_CENTRIC_CONSISTENCY_TEST (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing CLIENT_CENTRIC_CONSISTENCY_TEST (Requires 2 nodes)"
python3 $CLIENT_CENTRIC_CONSISTENCY_TEST || exit 1

echo End Client Centric Consistency Test
# ----------- END CLIENT CENTRIC CONSISTENCY TEST -----------

# ----------- BEGIN OFFLOAD CART TEST -----------
# Load data
countdown "Loading data for OFFLOAD_CART (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing OFFLOAD_CART (Requires 2 nodes)"
python3 $OFFLOAD_CART || exit 1

echo End Offload Cart Test
# ----------- END OFFLOAD CART TEST -----------

# ----------- BEGIN OFFLOAD BOTH TEST -----------
# Load data
countdown "Loading data for OFFLOAD_BOTH (Requires 3 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
with_context k3d-p1
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing OFFLOAD_BOTH (Requires 3 nodes)"
python3 $OFFLOAD_BOTH || exit 1

echo End Offload Both Test
# ----------- END OFFLOAD BOTH TEST -----------

# ----------- BEGIN OFFLOAD NEW SESSION TEST -----------
# Load data
countdown "Loading data for OFFLOAD_NEW_SESSION (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
execute_redis_commands $OFFLOADING_REJECT
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing OFFLOAD_NEW_SESSION (Requires 2 nodes)"
python3 $OFFLOAD_NEW_SESSION || exit 1

echo End Offload New Session Test
# ----------- END OFFLOAD NEW SESSION TEST -----------

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

# ----------- BEGIN OFFLOAD STRESS TEST -----------
# Load data
countdown "Loading data for OFFLOAD STRESS (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing OFFLOAD STRESS (Requires 2 nodes)"
python3 $OFFLOAD_STRESS || exit 1

echo End Offload Stress Test
# ----------- END OFFLOAD STRESS TEST -----------

