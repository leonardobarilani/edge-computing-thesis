from conftest import *


def test_function(setup_teardown):
	node_name = "k3d-p3"
	ip_address = get_ip(node_name)
	setup_trigger_node_metadata(node_name)
	setup_trigger_sessions_metadata(node_name)
	con = Connection(node_name=node_name, ip=ip_address)
	node_name = "k3d-p2"
	ip_address = get_ip(node_name)
	setup_node_metadata(node_name)

	while True:
		value = int(con.get('session-offloading-manager?command=offload-trigger'))
		if value > 0:
			continue
		else:
			assert value == -1
			break

	assert 0 == int(con.get('session-offloading-manager?command=offload-trigger'))
