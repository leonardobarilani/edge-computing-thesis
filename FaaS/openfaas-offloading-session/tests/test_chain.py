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
time.sleep(1)

assert Data.force_offload == con3.get('session-offloading-manager?command=force-offload', 
                                      headers={'X-forced-session':'marco'})
time.sleep(1)

assert Data.test_function_4 == con3.get('session-offloading-manager?command=test-function&type=session&value=' + session)
assert Data.test_function_5 == con2.get('session-offloading-manager?command=test-function&type=session&value=' + session)
assert Data.test_function_6 == con1.get('session-offloading-manager?command=test-function&type=session&value=' + session)

assert Data.force_onload == con3.get('session-offloading-manager?command=force-onload')
time.sleep(1)

assert Data.test_function_7 == con3.get('session-offloading-manager?command=test-function&type=session&value=' + session)
assert Data.test_function_8 == con2.get('session-offloading-manager?command=test-function&type=session&value=' + session)
assert Data.test_function_9 == con1.get('session-offloading-manager?command=test-function&type=session&value=' + session)
