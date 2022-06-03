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

session='barometro'

# string, string, (string,string)
def send(command, ip, auth):
	request = requests.get('http://' + ip + ':31112/function/' + command, auth=auth, timeout=3*60, headers={'X-session': session})
	print (command + " response ("+str(request.status_code)+"): \n" + str(request.content, "utf-8") + "\n")
	return str(request.content, "utf-8")

p3_ip = os.popen('minikube ip -p p3').read().translate(str.maketrans('', '', ' \n\t\r'))
p3_auth = ('admin','baDMaR9ByI7O')
p2_ip = os.popen('minikube ip -p p2').read().translate(str.maketrans('', '', ' \n\t\r'))
p2_auth = ('admin','gK2NxtzKpzbL')

print("Starting tests:\n\n")

send('session-offloading-manager?command=set-offload-status&status=accept', p3_ip, p3_auth)
send('session-offloading-manager?command=set-offload-status&status=accept', p2_ip, p2_auth)
send('session-offloading-manager?command=redis&redis-command=delete-all-sessions', p3_ip, p3_auth)
send('session-offloading-manager?command=redis&redis-command=delete-all-sessions', p2_ip, p2_auth)
input("Press Enter to continue...\n")

send('session-offloading-manager?command=test-function&session='+session, p3_ip, p3_auth)
send('session-offloading-manager?command=test-function&session='+session, p2_ip, p2_auth)
input("Press Enter to continue...\n")

for i in range(0, 3):
	for j in range(0, 2):
		send('iot-data-reducer?value='+str(i), p3_ip, p3_auth)
		input("Press Enter to continue...")

send('session-offloading-manager?command=force-offload', p3_ip, p3_auth)
input("Press Enter to continue...\n")

send('session-offloading-manager?command=test-function&session='+session, p3_ip, p3_auth)
send('session-offloading-manager?command=test-function&session='+session, p2_ip, p2_auth)
input("Press Enter to continue...\n")

for i in range(0, 3):
	for j in range(0, 2):
		send('iot-data-reducer?value='+str(i), p3_ip, p3_auth)
		input("Press Enter to continue...")

send('session-offloading-manager?command=test-function&session='+session, p3_ip, p3_auth)
send('session-offloading-manager?command=test-function&session='+session, p2_ip, p2_auth)
input("Press Enter to continue...\n")

send('session-offloading-manager?command=force-onload', p3_ip, p3_auth)
input("Press Enter to continue...\n")

send('session-offloading-manager?command=test-function&session='+session, p3_ip, p3_auth)
send('session-offloading-manager?command=test-function&session='+session, p2_ip, p2_auth)
input("Press Enter to continue...\n")

for i in range(0, 3):
	for j in range(0, 2):
		send('iot-data-reducer?value='+str(i), p3_ip, p3_auth)
		input("Press Enter to continue...")

send('session-offloading-manager?command=test-function&session='+session, p3_ip, p3_auth)
send('session-offloading-manager?command=test-function&session='+session, p2_ip, p2_auth)
input("Press Enter to continue...\n")

