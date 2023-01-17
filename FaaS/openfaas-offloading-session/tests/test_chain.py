# Test:
# Offload a session to the grandparent and access it succesfully

# API calls:
# p3 test-session
# p2 test-session
# p1 test-session
# p3 force-offload
# (p2 offload-session)
# (p1 offload-session)
# (p3 update-session)
# (p3 migrate-session)
# p3 test-session
# p2 test-session
# p1 test-session


from connection import Connection

session = 'marco'
con3 = Connection(node_name='k3d-p3', session=session)
con2 = Connection(node_name='k3d-p2', session=session)
con1 = Connection(node_name='k3d-p1', session=session)

input("Press Enter to begin test_chain.py...")

con3.get('session-offloading-manager?command=test-function&session='+session)
con2.get('session-offloading-manager?command=test-function&session='+session)
con1.get('session-offloading-manager?command=test-function&session='+session)
input("Press Enter to continue...")

con3.get('session-offloading-manager?command=force-offload')
input("Press Enter to continue...")

con3.get('session-offloading-manager?command=test-function&session='+session)
con2.get('session-offloading-manager?command=test-function&session='+session)
con1.get('session-offloading-manager?command=test-function&session='+session)
input("Press Enter to continue...")
