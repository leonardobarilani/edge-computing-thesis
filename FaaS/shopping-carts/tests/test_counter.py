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
con = Connection(node_name='k3d-p3')

send_get('session-offloading-manager?command=set-offload-status&status=accept', p2_ip, p2_auth)
input("Press Enter to continue...")

send_post('products-counter', p2_ip, p2_auth)
input("Press Enter to continue...")
