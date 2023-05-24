source $(dirname $BASH_SOURCE)/script-utils.sh

COMPLETE_CLEAN=$SCRIPT_PATH/complete-clean.txt
DEFAULT_CONFIG=$SCRIPT_PATH/default-config.txt
SIMPLE_TEST=$SCRIPT_PATH/test.py

# ----------- BEGIN SIMPLE TEST -----------
# Load data
countdown "Loading data for SIMPLE_TEST (Requires 2 nodes)"
with_context k3d-p3
execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG

# Execute test
countdown "Executing SIMPLE_TEST (Requires 2 nodes)"
python3 $SIMPLE_TEST || exit 1
xdg-open "http://127.0.0.1:8000/?file=mp4"
python3 $SCRIPT_PATH/proxy.py


echo End Simple Test
# ----------- END SIMPLE TEST -----------
