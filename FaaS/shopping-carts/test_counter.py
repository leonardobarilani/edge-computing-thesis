# Test:
# Create and delete a session

# API calls:
# set-offload-status
# create-session
# test-function
# delete-session
# test-function

import os
import requests


# string, string, (string,string)
def send_post(command, ip, auth):
    session = requests.post('http://' + ip + ':31112/function/' + command, auth=auth, timeout=5,
                            headers={'X-session': 'marco'}, data='cactus')
    print(command + " response: \n" + str(session.content, "utf-8") + "\n")
    return str(session.content, "utf-8")


def send_get(command, ip, auth):
    session = requests.get('http://' + ip + ':31112/function/' + command, auth=auth, timeout=5)
    print(command + " response: \n" + str(session.content, "utf-8") + "\n")
    return str(session.content, "utf-8")


p2_ip = os.popen('minikube ip -p p2').read().translate(str.maketrans('', '', ' \n\t\r'))
p2_auth = ('admin', 'DgFFPZdnCaMN')

send_get('session-offloading-manager?command=set-offload-status&status=accept', p2_ip, p2_auth)
input("Press Enter to continue...")

send_post('products-counter', p2_ip, p2_auth)
input("Press Enter to continue...")
