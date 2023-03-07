import time

from connection import Connection
from data_test import DataGarbageCollector as Data

session = 'marco'
con = Connection(node_name='k3d-p3')

assert Data.test_before == con.get('session-offloading-manager?command=test-function&type=session&value=' + session)

assert Data.garbage_collector == con.get('session-offloading-manager?command=garbage-collector')

assert Data.test_after == con.get('session-offloading-manager?command=test-function&type=session&value=' + session)
