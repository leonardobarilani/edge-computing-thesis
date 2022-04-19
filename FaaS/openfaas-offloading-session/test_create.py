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

# string, string, (string,string)
def send(command, ip, auth):
	session = requests.get('http://' + ip + ':31112/function/' + command, auth=auth, timeout=5)
	print (command + " response: \n" + str(session.content, "utf-8") + "\n")
	return str(session.content, "utf-8")
	

p3_ip = os.popen('minikube ip -p p3').read().translate(str.maketrans('', '', ' \n\t\r'))
p3_auth = ('admin','CCBKvk3zqhIs')

send('session-offloading-manager?command=set-offload-status&status=accept', p3_ip, p3_auth)
input("Press Enter to continue...")

session = send('session-offloading-manager?command=create-session', p3_ip, p3_auth)
input("Press Enter to continue...")

send('session-offloading-manager?command=test-function&session='+session, p3_ip, p3_auth)
input("Press Enter to continue...")

send('session-offloading-manager?command=delete-session&session='+session, p3_ip, p3_auth)
input("Press Enter to continue...")

send('session-offloading-manager?command=test-function&session='+session, p3_ip, p3_auth)
input("Press Enter to continue...")
