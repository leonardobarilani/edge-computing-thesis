import subprocess

import pytest
import redis

from connection import Connection


def get_ip(node_name):
    try:
        command = f'kubectl --context {node_name} get nodes -o jsonpath="{{.items[0].status.addresses[0].address}}"'
        result = subprocess.check_output(command, shell=True, text=True)
        return result.strip()
    except subprocess.CalledProcessError as e:
        pytest.fail(f'Failed to retrieve cluster IP for node "{node_name}": {e.output}')


@pytest.fixture
def setup_teardown():
    output = subprocess.check_output(['kubectl', 'config', 'get-contexts', '--no-headers'])
    contexts = output.decode().splitlines()

    for context in contexts:
        context_name = context.split()[1]
        ip_address = get_ip(context_name)

        # Define the Redis pod details
        redis_port = 6379  # Replace with the Redis port number

        # Connect to Redis
        redis_client = redis.Redis(host=ip_address, port=redis_port)

        # Flush all data
        redis_client.flushall(asynchronous=False)

        # Close the Redis connection
        redis_client.close()
    yield
    # Teardown code after each test


def setup_trigger_node_metadata(request):
    node_name = request
    ip_address = get_ip(node_name)
    redis_port = 6379  # Replace with the Redis port number

    # Connect to Redis
    redis_client = redis.Redis(host=ip_address, port=redis_port)

    # Create a pipeline
    pipeline = redis_client.pipeline()

    # Add commands to the pipeline
    pipeline.select(0)
    pipeline.set("offloading", "accept")
    pipeline.set("sessions_locks_expiration_time", 3600)
    pipeline.set("offload_top_threshold", 300)
    pipeline.set("offload_bottom_threshold", 1)
    pipeline.set("onload_threshold", 0)

    # Execute the pipeline commands
    pipeline.execute()

    # Close the Redis connection
    redis_client.close()


def setup_trigger_sessions_metadata(request):
    # Connect to Redis
    node_name = request
    ip_address = get_ip(node_name)
    redis_port = 6379  # Replace with the Redis port number

    # Connect to Redis
    redis_client = redis.Redis(host=ip_address, port=redis_port)

    # Create a pipeline
    pipeline = redis_client.pipeline()

    # Add commands to the pipeline
    pipeline.select(1)
    pipeline.hset("marco", mapping={
        "PROPRIETARY_LOCATION": "k3d-p3",
        "CURRENT_LOCATION": "k3d-p3",
        "TIMESTAMP_CREATION": "2023-05-03T10:30:00Z",
        "TIMESTAMP_LAST_ACCESS": "2023-05-04T10:30:00Z"
    })
    pipeline.hset("filippo", mapping={
        "PROPRIETARY_LOCATION": "k3d-p3",
        "CURRENT_LOCATION": "k3d-p3",
        "TIMESTAMP_CREATION": "2023-05-03T10:30:00Z",
        "TIMESTAMP_LAST_ACCESS": "2023-05-04T10:30:00Z"
    })
    pipeline.select(2)
    pipeline.hset("marco", mapping={
        "key1": "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
    })
    pipeline.hset("filippo", mapping={
        "key1": "012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789"
    })

    # Execute the pipeline commands
    pipeline.execute()

    # Close the Redis connection
    redis_client.close()


def set_offloading_reject(request):
    node_name = request
    ip_address = get_ip(node_name)
    redis_port = 6379  # Replace with the Redis port number

    # Connect to Redis
    redis_client = redis.Redis(host=ip_address, port=redis_port)

    # Create a pipeline
    pipeline = redis_client.pipeline()

    # Queue SELECT command for database 0 in the pipeline
    pipeline.select(0)

    # Queue DEL command for key "offloading" in the pipeline
    pipeline.set('offloading', 'reject')

    # Execute the pipeline
    pipeline.execute()

    # Close the Redis connection
    redis_client.close()


def setup_sessions_metadata(request):
    node_name = request
    # Define the Redis pod details
    ip_address = get_ip(node_name)
    redis_port = 6379  # Replace with the Redis port number

    # Connect to Redis
    redis_client = redis.Redis(host=ip_address, port=redis_port)

    # Create a pipeline
    pipeline = redis_client.pipeline()

    # Select Redis database 1
    pipeline.select(1)

    # Queue HSET command for database 1 in the pipeline
    pipeline.hset('marco', mapping={
        'PROPRIETARY_LOCATION': 'k3d-p3',
        'CURRENT_LOCATION': 'k3d-p3',
        'TIMESTAMP_CREATION': '2022-05-03T10:30:00Z',
        'TIMESTAMP_LAST_ACCESS': '2022-05-04T10:30:00Z'
    })

    # Select Redis database 2
    pipeline.select(2)

    # Queue HSET command for database 2 in the pipeline
    pipeline.hset('marco', mapping={
        'key1': 'value1',
        'key2': 'value2'
    })

    # Execute the pipeline and wait for completion
    pipeline.execute()

    # Close the Redis connection
    redis_client.close()


def setup_node_metadata(request):
    node_name = request
    ip_address = get_ip(node_name)
    redis_port = 6379  # Replace with the Redis port number

    # Connect to Redis
    redis_client = redis.Redis(host=ip_address, port=redis_port)

    # Select Redis database 0
    redis_client.select(0)

    # Create a pipeline
    pipeline = redis_client.pipeline()

    # Queue SET commands in the pipeline
    pipeline.set('offloading', 'accept')
    pipeline.set('sessions_locks_expiration_time', 3600)
    pipeline.set('sessions_data_expiration_time', 3600)
    pipeline.set('offload_top_threshold', 1073741824)
    pipeline.set('offload_bottom_threshold', 943718400)
    pipeline.set('onload_threshold', 524288000)

    # Execute the pipeline and wait for completion
    pipeline.execute()

    # Close the Redis connection
    redis_client.close()


'''


def pytest_sessionstart(session):
    """ Before session hook: Runs before the test session starts. """
    cluster.setup()


def pytest_sessionfinish(session, exitstatus):
    """ After session hook: Runs after all tests have been completed. """
    cluster.teardown()

'''
