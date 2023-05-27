from data_test import DataTest
from conftest import *


def test_function(setup_teardown):
    node_name = "k3d-p3"
    ip_address = get_ip(node_name)
    setup_sessions_metadata(node_name)
    con = Connection(node_name='k3d-p3', ip=ip_address)

    session = 'marco'

    assert DataTest.set_offload_status == con.get('session-offloading-manager?command=set-offload-status&status=accept')

    assert DataTest.test_function == con.get(
        'session-offloading-manager?command=test-function&type=session&value=' + session)
