class DataTest:
	set_offload_status = "Offloading status from <null> to <accept>"
	
	test_function = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Offloading status: accept
Session data: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}"""

class DataOffload:
	test_function_1 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Offloading status: null
Session data: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}"""
	
	test_function_2 = """Session <marco> doesn't exist
Offloading status: accept"""

	force_offload = """Offloading:
	k3d-p2
	{"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}"""
	
	test_function_3 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Offloading status: null
Session data: <data_not_on_this_node>"""
	
	test_function_4 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Offloading status: accept
Session data: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}"""

class DataOnload:
	test_function_1 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Offloading status: null
Session data: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}"""
	test_function_2 = """Session <marco> doesn't exist
Offloading status: accept"""

	force_offload = """Offloading:
	k3d-p2
	{"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}"""

	test_function_3 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Offloading status: null
Session data: <data_not_on_this_node>"""
	test_function_4 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Offloading status: accept
Session data: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}"""

	force_onload = """Unloaded:
	Old session: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
	New session: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}"""

	test_function_5 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Offloading status: null
Session data: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}"""
	test_function_6 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Offloading status: accept
Session data: <data_not_on_this_node>"""

class DataChain:
	test_function_1 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Offloading status: null
Session data: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}"""
	test_function_2 = """Session <marco> doesn't exist
Offloading status: reject"""
	test_function_3 = """Session <marco> doesn't exist
Offloading status: accept"""
	force_offload = """Offloading:
	k3d-p2
	{"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}"""
	test_function_4 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p1","status":"UNLOCKED"}
Offloading status: null
Session data: <data_not_on_this_node>"""
	test_function_5 = """Session <marco> doesn't exist
Offloading status: reject"""
	test_function_6 = """Session <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p1","status":"UNLOCKED"}
Offloading status: accept
Session data: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}"""
