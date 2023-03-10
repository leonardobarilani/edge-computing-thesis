import time

from connection import Connection
from data_test import DataTest as Data

session = 'marco'
con = Connection(node_name='k3d-p3')

assert Data.set_offload_status == con.get('session-offloading-manager?command=set-offload-status&status=accept')
# input("Press Enter to continue...")

time.sleep(1)

assert Data.test_function == con.get('session-offloading-manager?command=test-function&type=session&value=' + session)
