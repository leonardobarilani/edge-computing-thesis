# Test:
# Offload a session and access it succesfully

# API calls:
# p3 test-session
# p2 test-session
# p3 force-offload
# (p2 offload-session)
# (p3 update-session)
# (p3 migrate-session)
# p3 test-session
# p2 test-session


from connection import Connection

session = 'marco'
con3 = Connection(node_name='k3d-p3', session=session)
con2 = Connection(node_name='k3d-p2', session=session)

input("Press Enter to begin test_offload.py...")

con3.get('session-offloading-manager?command=test-function&session='+session)
con2.get('session-offloading-manager?command=test-function&session='+session)
input("Press Enter to continue...")

con3.get('session-offloading-manager?command=force-offload', extra_headers={'X-forced-session':'marco'})
input("Press Enter to continue...")

con3.get('session-offloading-manager?command=test-function&session='+session)
con2.get('session-offloading-manager?command=test-function&session='+session)
input("Press Enter to continue...")
