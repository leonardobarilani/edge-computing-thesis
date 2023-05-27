import pytest
import redis
import subprocess


@pytest.fixture
def setup_teardown(request):
    node_name = request.param
    try:
        command = f'kubectl --context {node_name} get nodes -o jsonpath="{{.items[0].status.addresses[0].address}}"'
        result = subprocess.check_output(command, shell=True, text=True)
        ip_address = result.strip()
    except subprocess.CalledProcessError as e:
        pytest.fail(f'Failed to retrieve cluster IP for node "{node_name}": {e.output}')

    # Define the Redis pod details
    redis_port = 6379  # Replace with the Redis port number

    # Connect to Redis
    redis_client = redis.Redis(host=ip_address, port=redis_port)

    # Flush all data
    redis_client.flushall(asynchronous=False)

    # Close the Redis connection
    redis_client.close()
    yield node_name
    # Teardown code after each test



@pytest.mark.parametrize('setup_teardown', ["k3d-p1"], indirect=True)
def test_function(setup_teardown):
    parameter_value = setup_teardown
    # Test implementation using the parameter value
