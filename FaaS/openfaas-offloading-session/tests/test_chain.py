# Test:
# Offload a session to the grandparent and access it succesfully

# API calls:
# p3 test-session
# p2 test-session
# p1 test-session
# p3 force-offload
# (p2 offload-session)
# (p1 offload-session)
# (p3 update-session)
# (p3 migrate-session)
# p3 test-session
# p2 test-session
# p1 test-session


import time

from connection import Connection
from data_test import DataChain as Data

session = 'marco'
con3 = Connection(node_name='k3d-p3')
con2 = Connection(node_name='k3d-p2')
con1 = Connection(node_name='k3d-p1')

assert Data.test_function_1 == con3.get('session-offloading-manager?command=test-function&type=session&value=' + session)
assert Data.test_function_2 == con2.get('session-offloading-manager?command=test-function&type=session&value=' + session)
assert Data.test_function_3 == con1.get('session-offloading-manager?command=test-function&type=session&value=' + session)
# input("Press Enter to continue...")
time.sleep(1)

assert Data.force_offload == con3.get('session-offloading-manager?command=force-offload', 
                                      headers={'X-forced-session':'marco'})
# input("Press Enter to continue...")
time.sleep(1)

assert Data.test_function_4 == con3.get('session-offloading-manager?command=test-function&type=session&value=' + session)
assert Data.test_function_5 == con2.get('session-offloading-manager?command=test-function&type=session&value=' + session)
assert Data.test_function_6 == con1.get('session-offloading-manager?command=test-function&type=session&value=' + session)
