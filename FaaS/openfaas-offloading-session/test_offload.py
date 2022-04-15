# Test:
# Offload a session and access it succesfully

# API calls:
# create-session
# force-offload
# test-session
# delete-session
# test-session

import requests
import os

p3_ip = os.popen('minikube ip -p p3').read().translate(str.maketrans('', '', ' \n\t\r'))
p3_auth = ('admin','dwTPP2jqw0HE')

offloading = requests.get('http://' + p3_ip + ':31112/function/session-offloading-manager?command=set-offload-status&status=no', auth=p3_auth, timeout=5)
print ("Session received: \n" + str(offloading.content))

session = requests.get('http://' + p3_ip + ':31112/function/session-offloading-manager?command=create-session', auth=p3_auth, timeout=5)
session_text = str(session.content)
print ("Session received: \n" + session_text)

force = requests.get('http://' + p3_ip + ':31112/function/session-offloading-manager?command=force-offload', auth=p3_auth, timeout=5)
print ("Force offload received: \n" + str(force.content))

test = requests.get('http://' + p3_ip + ':31112/function/session-offloading-manager?command=test-function', auth=p3_auth, timeout=5, params={"session":session.content})
print ("Test (should work): \n" + str(test.content))

delete = requests.get('http://' + p3_ip + ':31112/function/session-offloading-manager?command=delete-session', auth=p3_auth, timeout=5, params={"session":session.content})
print ("Delete message received: \n" + str(delete.content))

test = requests.get('http://' + p3_ip + ':31112/function/session-offloading-manager?command=test-function', auth=p3_auth, timeout=5, params={"session":session.content})
print ("Test (should fail): \n" + str(test.content))