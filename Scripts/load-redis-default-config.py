import argparse
import redis

# Parse command-line arguments
parser = argparse.ArgumentParser(description='Script to fill Redis table 0')
parser.add_argument('--host', type=str, default='localhost', help='Redis server host')
parser.add_argument('--port', type=int, default=6379, help='Redis server port')
args = parser.parse_args()

# Connect to Redis
try:
    r = redis.Redis(host=args.host, port=args.port)
    print(f'Connected to Redis ({args.host}:{args.port}).')
except redis.RedisError as e:
    print(f'Error connecting to Redis: {e}')
    exit(1)

# Execute Redis commands
try:
    r.select(0)
    r.set('offloading', 'accept')
    r.set('sessions_locks_expiration_time', 3600)
    r.set('sessions_data_expiration_time', 3600)
    r.set('offload_top_threshold', 1073741824)
    r.set('offload_bottom_threshold', 943718400)
    r.set('onload_threshold', 524288000)
    print('Commands executed successfully.')
except redis.RedisError as e:
    print(f'Error executing Redis commands: {e}')

# Close the Redis connection
r.close()
print('Redis connection closed.')

