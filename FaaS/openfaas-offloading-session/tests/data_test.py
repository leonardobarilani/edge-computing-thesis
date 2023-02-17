class DataTest:
    set_offload_status = "Offloading status from <null> to <accept>"

    test_function = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}
"""

class DataOffload:
    test_function_1 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}
"""

    test_function_2 = """Session metadata <marco>: <session_not_present_in_this_node>
Session data <marco>: {"session_data":[]}
"""

    force_offload = """Offloading:
k3d-p2
{"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"LOCKED"}"""


    test_function_3 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <marco>: {"session_data":[]}
"""

    test_function_4 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}
"""


class DataOnload:
    test_function_1 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}
"""
    test_function_2 = """Session metadata <marco>: <session_not_present_in_this_node>
Session data <marco>: {"session_data":[]}
"""

    force_offload = """Offloading:
k3d-p2
{"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"LOCKED"}"""

    test_function_3 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <marco>: {"session_data":[]}
"""
    test_function_4 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}
"""

    force_onload = """Unloaded:
Old session: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p2","status":"LOCKED"}
New session: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"LOCKED"}"""

    test_function_5 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}
"""
    test_function_6 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Session data <marco>: {"session_data":[]}
"""


class DataChain:
    test_function_1 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}
"""
    test_function_2 = """Session metadata <marco>: <session_not_present_in_this_node>
Session data <marco>: {"session_data":[]}
"""
    test_function_3 = """Session metadata <marco>: <session_not_present_in_this_node>
Session data <marco>: {"session_data":[]}
"""
    force_offload = """Offloading:
k3d-p2
{"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p3","status":"LOCKED"}"""
    test_function_4 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p1","status":"UNLOCKED"}
Session data <marco>: {"session_data":[]}
"""
    test_function_5 = """Session metadata <marco>: <session_not_present_in_this_node>
Session data <marco>: {"session_data":[]}
"""
    test_function_6 = """Session metadata <marco>: {"session":"marco","proprietaryLocation":"k3d-p3","currentLocation":"k3d-p1","status":"UNLOCKED"}
Session data <marco>: {"session_data":[{"key":"key1","data":"value1"},{"key":"key2","data":"value2"}]}
"""
