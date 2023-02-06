# Test:
# Change offload status and test a session

# API calls:
# p3 set-offload-status
# p3 test-function

import time

from connection import Connection
from data_test import DataTest as Data

session = 'marco'
con = Connection(node_name='k3d-p3', session=session)

assert Data.set_offload_status == con.get('session-offloading-manager?command=set-offload-status&status=accept')
# input("Press Enter to continue...")

time.sleep(1)

assert Data.test_function == con.get('session-offloading-manager?command=test-function&session=' + session)
