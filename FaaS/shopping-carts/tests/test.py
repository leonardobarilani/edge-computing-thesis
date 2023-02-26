from connection import Connection
from data_test import DataTest as Data

con3 = Connection(node_name='k3d-p3')
con2 = Connection(node_name='k3d-p2')

session1 = 'marco'
session2 = 'filippo'
session3 = 'counter-k3d-p2'

assert Data.cart1 == con3.get('shopping-cart?product=kiwi', headers={'X-session':session1})[0]
assert Data.cart2 == con3.get('shopping-cart?product=apple', headers={'X-session':session1})[0]
assert Data.cart3 == con3.get('shopping-cart?product=banana', headers={'X-session':session1})[0]
assert Data.cart4 == con3.get('shopping-cart?product=kiwi', headers={'X-session':session2})[0]
assert Data.cart5 == con3.get('shopping-cart?product=apple', headers={'X-session':session2})[0]
assert Data.cart6 == con3.get('shopping-cart?product=banana', headers={'X-session':session2})[0]

assert Data.test_function_1 == con2.get('session-offloading-manager?command=test-function&type=session&value='+session3)[0]

assert Data.cart7 == con3.get('shopping-cart?product=kiwi', headers={'X-session':session2})[0]
assert Data.cart8 == con3.get('shopping-cart?product=apple', headers={'X-session':session2})[0]
assert Data.cart9 == con3.get('shopping-cart?product=banana', headers={'X-session':session2})[0]

assert Data.test_function_2 == con2.get('session-offloading-manager?command=test-function&type=session&value='+session3)[0]
