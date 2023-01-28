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
	rm $TMP_FILEPATH > /dev/null
	touch $TMP_FILEPATH
	echo -n echo \'>> $TMP_FILEPATH
	while read line;
	do
		# echo "Appending redis command: $line"
		echo -n $line\\n>> $TMP_FILEPATH
		#kubectl exec -n openfaas-fn my-openfaas-redis-master-0 -- sh -c "redis-cli -a customRedisPassword $line"
	done < $1
	echo \' \| redis-cli >> $TMP_FILEPATH
}
function execute_redis_commands ()
{
	if [ $# -eq 0 ]
	then
	    echo execute_redis_commands: one argument needed: file_name
	    exit 1
	fi
	# echo Creating the generator script $1
	create_generator_script $1
	# echo Uploading the generator script to pod
	kubectl cp $TMP_FILEPATH openfaas-fn/my-openfaas-redis-master-0:/tmp || { echo;echo execute_redis_commands: Failed to upload generator script to pod;exit 1; }
	# echo Setting the generator script as executable
	kubectl exec -n openfaas-fn -it my-openfaas-redis-master-0 -- chmod +x /tmp/$TMP_FILENAME || { echo;echo execute_redis_commands: Failed to setting the generator script as executable;exit 1; }
	# echo Executing the generator script
	kubectl exec -n openfaas-fn -it my-openfaas-redis-master-0 -- sh -c /tmp/$TMP_FILENAME || { echo;echo execute_redis_commands: Failed executing the generator script;exit 1; }
	# echo Cleaning the generator script on pod
	sleep 1
	kubectl exec -n openfaas-fn -it my-openfaas-redis-master-0 -- rm /tmp/$TMP_FILENAME || { echo;echo execute_redis_commands: Failed cleaning the generator script on pod;exit 1; }
	# echo Cleaning the generator script locally
	rm $TMP_FILEPATH || { echo;echo execute_redis_commands: Failed cleaning the generator script locally;exit 1; }
}
function with_context ()
{
	if [ $# -eq 0 ]
	then
	    echo with_context: one argument needed: context
	    exit 1
	fi
	kubectl config use-context $1 || { echo;echo with_context: invalid context \"$1\" specified; exit 1; }
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
read -p "Press Enter to begin SIMPLE_TEST (Requires 1 node)"

# Load data
with_context k3d-p3
execute_redis_commands $CREATE_SESSION
execute_redis_commands $OFFLOADING_NULL

# Execute test
python3 $SIMPLE_TEST

# Delete data
execute_redis_commands $DELETE_SESSION
execute_redis_commands $OFFLOADING_NULL
echo End Simple Test
# ----------- END SIMPLE TEST -----------

# ----------- BEGIN OFFLOAD TEST -----------
read -p "Press Enter to begin OFFLOAD_TEST (Requires 2 nodes)"

# Load data
with_context k3d-p3
execute_redis_commands $CREATE_SESSION
with_context k3d-p2
execute_redis_commands $OFFLOADING_ACCEPT

# Execute test
python3 $OFFLOAD_TEST

# Delete data
with_context k3d-p2
execute_redis_commands $DELETE_SESSION
execute_redis_commands $OFFLOADING_NULL
echo End Offload Test
# ----------- END OFFLOAD TEST -----------

# ----------- BEGIN ONLOAD TEST -----------
read -p "Press Enter to begin ONLOAD_TEST (Requires 2 nodes)"

# Load data
with_context k3d-p2
execute_redis_commands $CREATE_SESSION

# Execute test
python3 $ONLOAD_TEST

# Delete data
with_context k3d-p3
execute_redis_commands $DELETE_SESSION
echo End Onload Test
# ----------- END ONLOAD TEST -----------

# ----------- BEGIN CHAIN TEST -----------
read -p "Press Enter to begin CHAIN_TEST (Requires 3 nodes)"

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
with_context k3d-p2
execute_redis_commands $OFFLOADING_NULL
with_context k3d-p1
execute_redis_commands $OFFLOADING_NULL
execute_redis_commands $DELETE_SESSION
echo End Chain Test
# ----------- END CHAIN TEST -----------

