from data_test import DataGarbageCollector
from conftest import *


def test_function(setup_teardown):
    node_name = "k3d-p3"
    ip_address = get_ip(node_name)
    setup_sessions_metadata(node_name)
    setup_node_metadata(node_name)
    con = Connection(node_name='k3d-p3', ip=ip_address)
    session = 'marco'

    assert DataGarbageCollector.test_before == con.get(
        'session-offloading-manager?command=test-function&type=session&value=' + session)

    assert DataGarbageCollector.garbage_collector == con.get('session-offloading-manager-garbage-collector?command=garbage-collector&deletePolicy=forced&sessionId=' + session)

    assert DataGarbageCollector.test_after == con.get(
        'session-offloading-manager?command=test-function&type=session&value=' + session)
