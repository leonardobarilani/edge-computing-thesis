import uuid
from retrying import RetryError
from connection import Connection
from data_test import DataTest as Data

con3 = Connection(node_name='k3d-p3')

session1 = 'marco'
uuid1 = str(uuid.uuid4())
uuid2 = str(uuid.uuid4())

assert 200 == con3.get('shopping-cart?product=kiwi', 
	headers={'X-session':session1, 'X-session-request-id':uuid1})[1]

assert 208 == con3.get('shopping-cart?product=kiwi', 
	headers={'X-session':session1, 'X-session-request-id':uuid1})[1]

try:
    assert 300 == con3.get('shopping-cart?product=kiwi', 
		headers={'X-session':session1, 'X-session-request-id':'wrongly formatted uuid'})[1]
except RetryError as e:
    print(f"An error occurred: {e}")

assert 200 == con3.get('session-offloading-manager?command=force-offload', 
    headers={'X-forced-session':session1})[1]

assert 200 == con3.get('shopping-cart?product=apple', 
	headers={'X-session':session1, 'X-session-request-id':uuid2})[1]

assert 200 == con3.get('session-offloading-manager?command=force-onload')[1]

assert 208 == con3.get('shopping-cart?product=kiwi', 
	headers={'X-session':session1, 'X-session-request-id':uuid1})[1]

assert 208 == con3.get('shopping-cart?product=apple', 
	headers={'X-session':session1, 'X-session-request-id':uuid2})[1]
