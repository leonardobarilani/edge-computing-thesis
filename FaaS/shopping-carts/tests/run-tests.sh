source $(dirname $BASH_SOURCE)/script-utils.sh

COMPLETE_CLEAN=$SCRIPT_PATH/complete-clean.txt
OFFLOADING_REJECT=$SCRIPT_PATH/offloading-reject.txt
OFFLOADING_NULL=$SCRIPT_PATH/offloading-null.txt
OFFLOADING_ACCEPT=$SCRIPT_PATH/offloading-accept.txt
SIMPLE_TEST=$SCRIPT_PATH/test.py
OFFLOAD_CART=$SCRIPT_PATH/test_offload_cart.py
OFFLOAD_BOTH=$SCRIPT_PATH/test_offload_both.py

# ----------- BEGIN SIMPLE TEST -----------
# Load data
countdown "Loading data for SIMPLE_TEST (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN

# Execute test
countdown "Executing SIMPLE_TEST (Requires 2 nodes)"
python3 $SIMPLE_TEST || exit 1

echo End Simple Test
# ----------- END SIMPLE TEST -----------

# ----------- BEGIN OFFLOAD CART TEST -----------
# Load data
countdown "Loading data for OFFLOAD_CART (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $OFFLOADING_ACCEPT

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
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $OFFLOADING_ACCEPT
with_context k3d-p1
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $OFFLOADING_ACCEPT

# Execute test
countdown "Executing OFFLOAD_BOTH (Requires 3 nodes)"
python3 $OFFLOAD_BOTH || exit 1

echo End Offload Both Test
# ----------- END OFFLOAD BOTH TEST -----------

