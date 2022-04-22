# Test:
# Offload a session with a reject in the middle and access it succesfully

# API calls:
# p3 redis delete-all-sessions
# p2 redis delete-all-sessions
# p1 redis delete-all-sessions
# p3 set-offload-status accept
# p2 set-offload-status reject
# p1 set-offload-status accept
# p3 create-session
# p3 force-offload
# (p2 offload-session)
# (p1 offload-session)
# (p3 update-session)
# p3 test-session
# p2 test-session
# p1 test-session
# p3 delete-session
# p3 test-session
# p1 test-session

import requests
import os
import time

# string, string, (string,string)
def send(command, ip, auth):
	session = requests.get('http://' + ip + ':31112/function/' + command, auth=auth, timeout=5)
	print (ip + "  " + command + " response: \n" + str(session.content, "utf-8") + "\n")
	return str(session.content, "utf-8")
	
p3_ip = os.popen('minikube ip -p p3').read().translate(str.maketrans('', '', ' \n\t\r'))
p3_auth = ('admin', 'AtwatNsxwnUw')

p2_ip = os.popen('minikube ip -p p2').read().translate(str.maketrans('', '', ' \n\t\r'))
p2_auth = ('admin', 'cU5X45xVOSql')

p1_ip = os.popen('minikube ip -p p1').read().translate(str.maketrans('', '', ' \n\t\r'))
p1_auth = ('admin', 'qrO18qNdfOrH')

send('session-offloading-manager?command=redis&redis-command=delete-all-sessions', p3_ip, p3_auth)
send('session-offloading-manager?command=redis&redis-command=delete-all-sessions', p2_ip, p2_auth)
send('session-offloading-manager?command=redis&redis-command=delete-all-sessions', p1_ip, p1_auth)
input("Press Enter to continue...")

send('session-offloading-manager?command=set-offload-status&status=accept', p3_ip, p3_auth)
send('session-offloading-manager?command=set-offload-status&status=reject', p2_ip, p2_auth)
send('session-offloading-manager?command=set-offload-status&status=accept', p1_ip, p1_auth)
input("Press Enter to continue...")

session = send('session-offloading-manager?command=create-session', p3_ip, p3_auth)
input("Press Enter to continue...")

send('session-offloading-manager?command=force-offload', p3_ip, p3_auth)
input("Press Enter to continue...")

send('session-offloading-manager?command=test-function&session='+session, p3_ip, p3_auth)
send('session-offloading-manager?command=test-function&session='+session, p2_ip, p2_auth)
send('session-offloading-manager?command=test-function&session='+session, p1_ip, p1_auth)
input("Press Enter to continue...")

send('session-offloading-manager?command=delete-session&session='+session, p3_ip, p3_auth)
input("Press Enter to continue...")

send('session-offloading-manager?command=test-function&session='+session, p3_ip, p3_auth)
send('session-offloading-manager?command=test-function&session='+session, p1_ip, p1_auth)
