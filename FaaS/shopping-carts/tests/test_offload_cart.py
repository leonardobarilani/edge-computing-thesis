from connection import Connection
from data_test import DataOffloadCart as Data

con3 = Connection(node_name='k3d-p3')
con2 = Connection(node_name='k3d-p2')

session1 = 'marco'

assert Data.cart1 == con3.get('shopping-cart?product=kiwi', headers={'X-session':session1})

assert Data.offload == con3.get('session-offloading-manager?command=force-offload', 
    headers={'X-forced-session':session1})

assert Data.test1 == con2.get('session-offloading-manager?command=test-function&session=' + session1)

assert Data.cart2 == con3.get('shopping-cart?product=banana', headers={'X-session':session1})
assert Data.cart3 == con2.get('shopping-cart?product=apple', headers={'X-session':session1})

assert Data.test2 == con2.get('session-offloading-manager?command=test-function&session=' + session1)

assert Data.onload == con3.get('session-offloading-manager?command=force-onload')

assert Data.test3 == con2.get('session-offloading-manager?command=test-function&session=' + session1)

assert Data.cart4 == con2.get('shopping-cart?product=orange', headers={'X-session':session1})
assert Data.cart5 == con3.get('shopping-cart?product=pear', headers={'X-session':session1})