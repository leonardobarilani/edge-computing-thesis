# Test:
# Change offload status and test a session

# API calls:
# p3 set-offload-status
# p3 test-function

from connection import Connection

session = 'marco'
con = Connection(node_name='k3d-p3', session=session)

input("Press Enter to begin test.py...")

con.get('session-offloading-manager?command=set-offload-status&status=accept')
input("Press Enter to continue...")

con.get('session-offloading-manager?command=test-function&session='+session)
input("Press Enter to continue...")
