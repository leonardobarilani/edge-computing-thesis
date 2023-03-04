import time

from connection import Connection

session = 'marco'
con = Connection(node_name='k3d-p3')

while True:
	value = int(con.get('session-offloading-manager?command=offload-trigger'))
	time.sleep(1)
	if value > 0:
		continue
	else:
		assert value == -1
		break

assert 0 == int(con.get('session-offloading-manager?command=offload-trigger'))
