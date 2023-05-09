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

session = 'marco'


# string, string, (string,string)
def send(command, ip, auth):
    response = requests.get('http://' + ip + ':31112/function/' + command, auth=auth, timeout=60,
                            headers={'X-session': session})
    print(command + " response: \n" + str(response.content, "utf-8") + "\n")
    return str(response.content, "utf-8")


p3_ip = os.popen('minikube ip -p p3').read().translate(str.maketrans('', '', ' \n\t\r'))
p3_auth = ('admin', 'baDMaR9ByI7O')
p2_ip = os.popen('minikube ip -p p2').read().translate(str.maketrans('', '', ' \n\t\r'))
p2_auth = ('admin', 'gK2NxtzKpzbL')

send('session-offloading-manager?command=set-offload-status&status=accept', p3_ip, p3_auth)
send('session-offloading-manager?command=set-offload-status&status=accept', p2_ip, p2_auth)
send('session-offloading-manager?command=redis&redis-command=delete-all-sessions', p3_ip, p3_auth)
send('session-offloading-manager?command=redis&redis-command=delete-all-sessions', p2_ip, p2_auth)
input("Press Enter to continue...\n")

send('search-analytics-performer', p2_ip, p2_auth)
input("Press Enter to continue...\n")

for i in range(0, 1):  # 2):
    for j in range(0, 1):  # random.randint(1, 3)):
        send('search-analytics-data-receivers?search=keyword' + str(i), p3_ip, p3_auth)
        input("Press Enter to continue...")

send('search-analytics-performer', p2_ip, p2_auth)
input("Press Enter to continue...\n")
