source $(dirname $BASH_SOURCE)/script-utils.sh

COMPLETE_CLEAN=$SCRIPT_PATH/complete-clean.txt
CREATE_SESSION_P2=$SCRIPT_PATH/create-session-p2.txt
CREATE_SESSION_P3=$SCRIPT_PATH/create-session-p3.txt
SESSIONS_TRIGGER_TEST=$SCRIPT_PATH/sessions-trigger-test.txt
OFFLOADING_REJECT=$SCRIPT_PATH/offloading-reject.txt
OFFLOADING_ACCEPT=$SCRIPT_PATH/offloading-accept.txt
OFFLOADING_NULL=$SCRIPT_PATH/offloading-null.txt
DEFAULT_CONFIG=$SCRIPT_PATH/default-config.txt
TRIGGER_CONFIG=$SCRIPT_PATH/trigger-config.txt
TRIGGER_ONLOAD_CONFIG=$SCRIPT_PATH/trigger-onload-config.txt
SIMPLE_TEST=$SCRIPT_PATH/test.py
OFFLOAD_TEST=$SCRIPT_PATH/test_offload.py
ONLOAD_TEST=$SCRIPT_PATH/test_onload.py
CHAIN_TEST=$SCRIPT_PATH/test_chain.py
TRIGGER_TEST=$SCRIPT_PATH/test_trigger.py
GARBAGE_COLLECTOR_TEST=$SCRIPT_PATH/test_garbage_collector.py

# ----------- BEGIN SIMPLE TEST -----------
# Load data
countdown "Loading data for SIMPLE_TEST (Requires 1 node)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $CREATE_SESSION_P3
execute_redis_commands $OFFLOADING_NULL

# Execute test
countdown "Executing SIMPLE_TEST (Requires 1 node)"
python3 $SIMPLE_TEST || exit 1

echo End Simple Test
# ----------- END SIMPLE TEST -----------

# ----------- BEGIN GARBAGE COLLECTOR TEST -----------
# Load data
countdown "Loading data for GARBAGE_COLLECTOR_TEST (Requires 1 node)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
execute_redis_commands $CREATE_SESSION_P3

# Execute test
countdown "Executing GARBAGE_COLLECTOR_TEST (Requires 1 node)"
python3 $GARBAGE_COLLECTOR_TEST || exit 1

echo End Garbage Collector Test
# ----------- END GARBAGE COLLECTOR TEST -----------

# ----------- BEGIN OFFLOAD TEST -----------
# Load data
countdown "Loading data for OFFLOAD_TEST (Requires 2 node)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
execute_redis_commands $CREATE_SESSION_P3
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing OFFLOAD_TEST (Requires 2 node)"
python3 $OFFLOAD_TEST || exit 1

echo End Offload Test
# ----------- END OFFLOAD TEST -----------

# ----------- BEGIN ONLOAD TEST -----------
# Load data
countdown "Loading data for ONLOAD_TEST (Requires 2 node)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
execute_redis_commands $CREATE_SESSION_P3
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing ONLOAD_TEST (Requires 2 node)"
python3 $ONLOAD_TEST || exit 1

echo End Onload Test
# ----------- END ONLOAD TEST -----------

# ----------- BEGIN CHAIN TEST -----------
# Load data
countdown "Loading data for CHAIN_TEST (Requires 3 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
execute_redis_commands $CREATE_SESSION_P3
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG
execute_redis_commands $OFFLOADING_REJECT
with_context k3d-p1
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing CHAIN_TEST (Requires 3 nodes)"
python3 $CHAIN_TEST || exit 1

echo End Chain Test
# ----------- END CHAIN TEST -----------

# ----------- BEGIN TRIGGER TEST -----------
# Load data
countdown "Loading data for TRIGGER_TEST (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $TRIGGER_CONFIG
execute_redis_commands $SESSIONS_TRIGGER_TEST
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing TRIGGER_TEST (Requires 2 nodes)"
python3 $TRIGGER_TEST || exit 1

echo End Trigger Test
# ----------- END TRIGGER TEST -----------