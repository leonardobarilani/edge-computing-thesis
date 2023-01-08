# Test:
# Create and delete a session

# API calls:
# set-offload-status (success)
# test-function (fail)

from connection import Connection

session = 'marco'
con = Connection(node_name='k3d-p3', session=session)

con.get('session-offloading-manager?command=set-offload-status&status=accept')
input("Press Enter to continue...")

con.get('session-offloading-manager?command=test-function&session='+session)
input("Press Enter to continue...")
