import requests
import os
import time

# string, string, (string,string)
def send(command, ip, auth):
	session = requests.post('http://' + ip + ':31112/function/' + command, auth=auth, headers={'X-session': 'marco'}, data='data')
	print (command + " response: \n" + str(session.content, "utf-8") + "\n")
	return str(session.content, "utf-8")
	

ip_command = 'kubectl config use-context k3d-p3 > /dev/null && kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"'
p3_ip = os.popen(ip_command).read().translate(str.maketrans('', '', ' \n\t\r'))
p3_auth = ('admin','password')

send('stateful-append', p3_ip, p3_auth)
input("Press Enter to continue...")

