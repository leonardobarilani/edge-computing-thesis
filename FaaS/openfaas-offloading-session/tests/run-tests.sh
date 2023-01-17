SCRIPT_PATH=$(dirname $BASH_SOURCE)
TMP_FILENAME=init-test.sh
TMP_FILEPATH=$SCRIPT_PATH/$TMP_FILENAME
function create_generator_script ()
{
	if [ $# -eq 0 ]
	then
	    echo create_generator_script: one argument needed: file_name
	    exit 1
	fi
	touch $TMP_FILEPATH
	echo -n echo \'>> $TMP_FILEPATH
	while read line;
	do
		echo "Appending redis command: $line"
		echo -n $line\\n>> $TMP_FILEPATH
		#kubectl exec -n openfaas-fn my-openfaas-redis-master-0 -- sh -c "redis-cli -a customRedisPassword $line"
	done < $1
	echo -n \' \| redis-cli -a customRedisPassword>> $TMP_FILEPATH
}
function execute_redis_commands ()
{
	if [ $# -eq 0 ]
	then
	    echo execute_redis_commands: one argument needed: file_name
	    exit 1
	fi
	echo Creating the generator script
	create_generator_script $1
	echo Uploading the generator script to pod
	kubectl cp $TMP_FILEPATH openfaas-fn/my-openfaas-redis-master-0:/tmp
	echo Setting the generator script as executable
	kubectl exec -n openfaas-fn -it my-openfaas-redis-master-0 -- chmod +x /tmp/$TMP_FILENAME
	echo Executing the generator script
	kubectl exec -n openfaas-fn -it my-openfaas-redis-master-0 -- sh -c /tmp/$TMP_FILENAME
	echo Cleaning the generator script on pod
	kubectl exec -n openfaas-fn -it my-openfaas-redis-master-0 -- rm /tmp/$TMP_FILENAME
	echo Cleaning the generator script locally
	rm $TMP_FILEPATH
}
function with_context ()
{
	if [ $# -eq 0 ]
	then
	    echo with_context: one argument needed: context
	    exit 1
	fi
	kubectl config use-context $1 || echo with_context: invalid context \"$1\" specified; exit 1
}

CREATE_SESSION=$SCRIPT_PATH/create-session.txt
DELETE_SESSION=$SCRIPT_PATH/delete-session.txt
OFFLOADING_REJECT=$SCRIPT_PATH/offloading-reject.txt
OFFLOADING_NULL=$SCRIPT_PATH/offloading-null.txt
OFFLOADING_ACCEPT=$SCRIPT_PATH/offloading-accept.txt
SIMPLE_TEST=$SCRIPT_PATH/test.py
OFFLOAD_TEST=$SCRIPT_PATH/test_offload.py
ONLOAD_TEST=$SCRIPT_PATH/test_onload.py
CHAIN_TEST=$SCRIPT_PATH/test_chain.py

# ----------- BEGIN SIMPLE TEST -----------
# Load data
with_context k3d-p3
execute_redis_commands $CREATE_SESSION
execute_redis_commands $OFFLOADING_ACCEPT

# Execute test
python3 $SIMPLE_TEST

# Delete data
execute_redis_commands $DELETE_SESSION
execute_redis_commands $OFFLOADING_NULL
# ----------- END SIMPLE TEST -----------

# All the below tests require 2 or 3 nodes. The tests still need refinement
exit 1 

# ----------- BEGIN OFFLOAD TEST -----------
# Load data
with_context k3d-p3
execute_redis_commands $CREATE_SESSION
with_context k3d-p2
execute_redis_commands $OFFLOADING_ACCEPT

# Execute test
python3 $OFFLOAD_TEST

# Delete data
with_context k3d-p3
execute_redis_commands $DELETE_SESSION
with_context k3d-p2
execute_redis_commands $OFFLOADING_NULL
# ----------- END OFFLOAD TEST -----------

# ----------- BEGIN ONLOAD TEST -----------
# Load data
with_context k3d-p3
execute_redis_commands $CREATE_SESSION
with_context k3d-p2
execute_redis_commands $OFFLOADING_ACCEPT

# Execute test
python3 $ONLOAD_TEST

# Delete data
with_context k3d-p3
execute_redis_commands $DELETE_SESSION
with_context k3d-p2
execute_redis_commands $OFFLOADING_NULL
execute_redis_commands $DELETE_SESSION
# ----------- END ONLOAD TEST -----------

# ----------- BEGIN CHAIN TEST -----------
# Load data
with_context k3d-p3
execute_redis_commands $CREATE_SESSION
with_context k3d-p2
execute_redis_commands $OFFLOADING_REJECT
with_context k3d-p1
execute_redis_commands $OFFLOADING_ACCEPT

# Execute test
python3 $CHAIN_TEST

# Delete data
with_context k3d-p3
execute_redis_commands $DELETE_SESSION
with_context k3d-p2
execute_redis_commands $OFFLOADING_NULL
with_context k3d-p1
execute_redis_commands $OFFLOADING_NULL
execute_redis_commands $DELETE_SESSION
# ----------- END CHAIN TEST -----------
