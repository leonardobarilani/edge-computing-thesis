from data_test import DataOffload
from conftest import *


def test_function(setup_teardown):
    node_name = "k3d-p3"
    ip_address = get_ip(node_name)
    setup_sessions_metadata(node_name)
    setup_node_metadata(node_name)
    con3 = Connection(node_name=node_name, ip=ip_address)
    node_name = "k3d-p2"
    ip_address = get_ip(node_name)
    setup_node_metadata(node_name)
    con2 = Connection(node_name=node_name, ip=ip_address)

    session = 'marco'

    assert DataOffload.test_function_1 == con3.get(
        'session-offloading-manager?command=test-function&type=session&value=' + session)
    assert DataOffload.test_function_2 == con2.get(
        'session-offloading-manager?command=test-function&type=session&value=' + session)

    assert DataOffload.force_offload == con3.get('session-offloading-manager?command=force-offload',
                                                 headers={'X-forced-session': 'marco'})

    assert DataOffload.test_function_3 == con3.get(
        'session-offloading-manager?command=test-function&type=session&value=' + session)
    assert DataOffload.test_function_4 == con2.get(
        'session-offloading-manager?command=test-function&type=session&value=' + session)
