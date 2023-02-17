
class DataTest:
	cart1 = "[kiwi]"
	cart2 = "[kiwi, apple]"
	cart3 = "[kiwi, apple, banana]"
	cart4 = "[kiwi]"
	cart5 = "[kiwi, apple]"
	cart6 = "[kiwi, apple, banana]"

	test_function_1 = """Session metadata <counter-k3d-p2>: {"session":"counter-k3d-p2","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <counter-k3d-p2>: {"session_data":[{"key":"kiwi","data":"2"},{"key":"apple","data":"2"},{"key":"banana","data":"2"}]}
"""

	cart7 = "[kiwi, apple, banana, kiwi]"
	cart8 = "[kiwi, apple, banana, kiwi, apple]"
	cart9 = "[kiwi, apple, banana, kiwi, apple, banana]"
	
	test_function_2 = """Session metadata <counter-k3d-p2>: {"session":"counter-k3d-p2","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <counter-k3d-p2>: {"session_data":[{"key":"kiwi","data":"3"},{"key":"apple","data":"3"},{"key":"banana","data":"3"}]}
"""

class DataOffloadCart:
	cart1 = "[kiwi]"

	offload = """Offloading:
k3d-p2
{"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"LOCKED"}"""

	test1 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"products","data":"{\\"list\\":[\\"kiwi\\"]}"}]}
"""

	cart2 = "[kiwi, banana]"

	cart3 = "[kiwi, banana, apple]"

	test2 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"products","data":"{\\"list\\":[\\"kiwi\\",\\"banana\\",\\"apple\\"]}"}]}
"""

	onload = """Unloaded:
Old session: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"LOCKED"}
New session: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"LOCKED"}"""

	test3 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Session data <marco>: {"session_data":[]}
"""

	cart4 = "[kiwi, banana, apple, orange]"

	cart5 = "[kiwi, banana, apple, orange, pear]"

class DataOffloadBoth:
	cart1 = "[kiwi]"

	offload1 = """Offloading:
k3d-p2
{"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"LOCKED"}"""

	offload2 = """Offloading:
k3d-p1
{"session":"counter-k3d-p2","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p2","status":"LOCKED"}"""

	test1 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"products","data":"{\\"list\\":[\\"kiwi\\"]}"}]}
"""

	test2 = """Session metadata <counter-k3d-p2>: {"session":"counter-k3d-p2","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p1","status":"UNLOCKED"}
Session data <counter-k3d-p2>: {"session_data":[{"key":"kiwi","data":"1"}]}
"""

	cart2 = "[kiwi, banana]"

	test3 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"products","data":"{\\"list\\":[\\"kiwi\\",\\"banana\\"]}"}]}
"""

	test4 = """Session metadata <counter-k3d-p2>: {"session":"counter-k3d-p2","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p1","status":"UNLOCKED"}
Session data <counter-k3d-p2>: {"session_data":[{"key":"kiwi","data":"1"},{"key":"banana","data":"1"}]}
"""

	onload1 = """Unloaded:
Old session: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"LOCKED"}
New session: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"LOCKED"}"""

	onload2 = """Unloaded:
Old session: {"session":"counter-k3d-p2","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p1","status":"LOCKED"}
New session: {"session":"counter-k3d-p2","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p2","status":"LOCKED"}"""

	cart3 = "[kiwi, banana, orange]"

	cart4 = "[kiwi, banana, orange, pear]"

	test5 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"products","data":"{\\"list\\":[\\"kiwi\\",\\"banana\\",\\"orange\\",\\"pear\\"]}"}]}
"""

	test6 = """Session metadata <counter-k3d-p2>: {"session":"counter-k3d-p2","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <counter-k3d-p2>: {"session_data":[{"key":"banana","data":"1"},{"key":"kiwi","data":"1"},{"key":"orange","data":"1"},{"key":"pear","data":"1"}]}
"""
