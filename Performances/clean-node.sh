source $(dirname $BASH_SOURCE)/script-utils.sh

COMPLETE_CLEAN=$SCRIPT_PATH/complete-clean.txt
DEFAULT_CONFIG=$SCRIPT_PATH/default-config.txt

execute_redis_commands $COMPLETE_CLEAN
execute_redis_commands $DEFAULT_CONFIG