source $(dirname $BASH_SOURCE)/script-utils.sh

COMPLETE_CLEAN=$SCRIPT_PATH/complete-clean.txt
OFFLOADING_REJECT=$SCRIPT_PATH/offloading-reject.txt
OFFLOADING_NULL=$SCRIPT_PATH/offloading-null.txt
OFFLOADING_ACCEPT=$SCRIPT_PATH/offloading-accept.txt
SIMPLE_TEST=$SCRIPT_PATH/test.py

# ----------- BEGIN SIMPLE TEST -----------
# Load data
countdown "Loading data for SIMPLE_TEST (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
with_context k3d-p2
execute_redis_commands $COMPLETE_CLEAN

# Execute test
countdown "Executing SIMPLE_TEST (Requires 1 node)"
python3 $SIMPLE_TEST || exit 1

echo End Simple Test
# ----------- END SIMPLE TEST -----------

