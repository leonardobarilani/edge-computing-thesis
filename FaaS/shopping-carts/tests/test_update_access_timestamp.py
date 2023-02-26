import json
import time
from connection import Connection

con3 = Connection(node_name='k3d-p3')

session1 = 'marco'

con3.get('shopping-cart?product=kiwi', headers={'X-session':session1})[0]

res1 = con3.get('session-offloading-manager?command=test-function&type=sessionMetadata&value='+session1)[0]

time.sleep(2)

con3.get('shopping-cart?product=apple', headers={'X-session':session1})[0]

res2 = con3.get('session-offloading-manager?command=test-function&type=sessionMetadata&value='+session1)[0]
 
decoded1 = json.loads(res1[25:])
decoded2 = json.loads(res2[25:])
assert decoded1['timestampLastAccess'] != decoded2['timestampLastAccess']