
class DataTest:
	cart1 = "[kiwi]"
	cart2 = "[kiwi, apple]"
	cart3 = "[kiwi, apple, banana]"
	cart4 = "[kiwi]"
	cart5 = "[kiwi, apple]"
	cart6 = "[kiwi, apple, banana]"

	test_function_1 = """Session <counter>: {"session":"counter","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p2","status":"UNLOCKED"}
Offloading status: null
Session data: {"session_data":[{"key":"kiwi","data":"2"},{"key":"apple","data":"2"},{"key":"banana","data":"2"}]}"""

	cart7 = "[kiwi, apple, banana, kiwi]"
	cart8 = "[kiwi, apple, banana, kiwi, apple]"
	cart9 = "[kiwi, apple, banana, kiwi, apple, banana]"
	
	test_function_2 = """Session <counter>: {"session":"counter","proprietaryLocation":"k3d-p2","currentLocation":"k3d-p2","status":"UNLOCKED"}
Offloading status: null
Session data: {"session_data":[{"key":"kiwi","data":"3"},{"key":"apple","data":"3"},{"key":"banana","data":"3"}]}"""
