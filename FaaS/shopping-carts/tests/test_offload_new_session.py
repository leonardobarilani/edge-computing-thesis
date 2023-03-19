import uuid
from connection import Connection
from data_test import DataOffloadNewSession as Data

con3 = Connection(node_name='k3d-p3')
con2 = Connection(node_name='k3d-p2')

session1 = 'marco'

assert Data.cart1 == con3.get('shopping-cart?product=kiwi', 
    headers={'X-session':session1,'X-session-request-id':str(uuid.uuid4())})[0]

assert Data.test1 == con3.get('session-offloading-manager?command=test-function&type=session&value=' + session1)[0]

assert Data.test2 == con2.get('session-offloading-manager?command=test-function&type=session&value=' + session1)[0]
