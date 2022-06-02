# Test:
# Create and delete a session

# API calls:
# set-offload-status
# create-session
# test-function
# delete-session
# test-function

import requests
import os
import time
import random

session = 'marco'

# string, string, (string,string)
def send(command, ip, auth):
	response = requests.get('http://' + ip + ':31112/function/' + command, auth=auth, timeout=60, headers={'X-session': session})
	print (command + " response: \n" + str(response.content, "utf-8") + "\n")
	return str(response.content, "utf-8")

# string, string, (string,string), string
def send_post(command, ip, auth, data):
	response = requests.post('http://' + ip + ':31112/function/' + command, auth=auth, timeout=5, headers={'X-session': session}, data=data)
	print (command + " response: \n" + str(response.content, "utf-8") + "\n")
	return str(response.content, "utf-8")
	

p3_ip = os.popen('minikube ip -p p3').read().translate(str.maketrans('', '', ' \n\t\r'))
p3_auth = ('admin','baDMaR9ByI7O')
p2_ip = os.popen('minikube ip -p p2').read().translate(str.maketrans('', '', ' \n\t\r'))
p2_auth = ('admin','gK2NxtzKpzbL')

send_post('search-analytics-store-data', p2_ip, p2_auth, 'somethingsomething')
input("Press Enter to continue...\n")
