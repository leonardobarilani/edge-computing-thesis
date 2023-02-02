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
	if [ -f "$TMP_FILEPATH" ]; then
		rm $TMP_FILEPATH
	fi
	touch $TMP_FILEPATH
	echo -n echo \'>> $TMP_FILEPATH
	while read line;
	do
		# echo "Appending redis command: $line"
		echo -n $line\\n>> $TMP_FILEPATH
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
	create_generator_script $1
	kubectl exec -n openfaas-fn -it my-openfaas-redis-master-0 -- \
		sh -c "rm /tmp/$TMP_FILENAME 2> /dev/null ; echo $(base64 -w 0 $TMP_FILEPATH) | base64 --decode > /tmp/$TMP_FILENAME ; chmod +x /tmp/$TMP_FILENAME ; sh -c /tmp/$TMP_FILENAME ; rm /tmp/$TMP_FILENAME" \
		|| { echo;echo execute_redis_commands: Failed running remote commands to setup redis;exit 1; }
	rm $TMP_FILEPATH || { echo;echo execute_redis_commands: Failed cleaning the generator script locally;exit 1; }
	echo "echo Finished setting up Redis" >> $TMP_FILEPATH
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
function countdown ()
{
	printf "$1 in 5"
	sleep 1
	printf "\r$1 in 4"
	sleep 1
	printf "\r$1 in 3"
	sleep 1
	printf "\r$1 in 2"
	sleep 1
	printf "\r$1 in 1"
	sleep 1
	printf "\r$1     \n"
}
