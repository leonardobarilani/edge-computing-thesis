from connection import Connection
from data_test import DataOffloadBoth as Data

con3 = Connection(node_name='k3d-p3')
con2 = Connection(node_name='k3d-p2')
con1 = Connection(node_name='k3d-p1')

session1 = 'marco'
session2 = 'counter-k3d-p2'

assert Data.cart1 == con3.get('shopping-cart?product=kiwi', headers={'X-session':session1})

assert Data.offload1 == con3.get('session-offloading-manager?command=force-offload', 
    headers={'X-forced-session':session1})
assert Data.offload2 == con2.get('session-offloading-manager?command=force-offload', 
    headers={'X-forced-session':session2})

assert Data.test1 == con2.get('session-offloading-manager?command=test-function&session=' + session1)
assert Data.test2 == con1.get('session-offloading-manager?command=test-function&session=' + session2)

assert Data.cart2 == con3.get('shopping-cart?product=banana', headers={'X-session':session1})

assert Data.test3 == con2.get('session-offloading-manager?command=test-function&session=' + session1)
assert Data.test4 == con1.get('session-offloading-manager?command=test-function&session=' + session2)

assert Data.onload1 == con3.get('session-offloading-manager?command=force-onload')
assert Data.onload2 == con2.get('session-offloading-manager?command=force-onload')

assert Data.cart3 == con2.get('shopping-cart?product=orange', headers={'X-session':session1})
assert Data.cart4 == con3.get('shopping-cart?product=pear', headers={'X-session':session1})

assert Data.test5 == con3.get('session-offloading-manager?command=test-function&session=' + session1)
assert Data.test6 == con2.get('session-offloading-manager?command=test-function&session=' + session2)
