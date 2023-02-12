import time

from connection import Connection

con3 = Connection(node_name='k3d-p3')
con2 = Connection(node_name='k3d-p2')

session1 = 'marco'
session2 = 'filippo'

con3.get('shopping-cart?product=kiwi', headers={'X-session':session1})
con3.get('shopping-cart?product=apple', headers={'X-session':session1})
con3.get('shopping-cart?product=banana', headers={'X-session':session1})
con3.get('shopping-cart?product=kiwi', headers={'X-session':session2})
con3.get('shopping-cart?product=apple', headers={'X-session':session2})
con3.get('shopping-cart?product=banana', headers={'X-session':session2})

con2.get('session-offloading-manager?command=test-function&session=counter')

con3.get('shopping-cart?product=kiwi', headers={'X-session':session2})
con3.get('shopping-cart?product=apple', headers={'X-session':session2})
con3.get('shopping-cart?product=banana', headers={'X-session':session2})

con2.get('session-offloading-manager?command=test-function&session=counter')
