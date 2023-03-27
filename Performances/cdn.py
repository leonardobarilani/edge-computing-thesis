import time
import csv
import argparse
import matplotlib.pyplot as plt
import numpy as np
from datetime import datetime, timezone
import threading
from cdn_lib import *


parser = argparse.ArgumentParser(description='Make requests to a server and save results to a CSV file.')
parser.add_argument('--requests', type=int, default=100, help='Number of requests to make per client')
parser.add_argument('--clients', type=int, default=5, help='Number of clients')
parser.add_argument('--offloaded', type=float, default=0, help='Percentage of offloaded sessions: [0..1]')
parser.add_argument('--sessions', type=int, default=10, help='Number of sessions')
parser.add_argument('--filespersession', type=int, default=10, help='Number of files in a session')
parser.add_argument('--kbperfile', type=int, default=5, help='Kilobytes in a file')

args = parser.parse_args()

requests_per_client = args.requests
clients_count = args.clients
offloaded_percentage = args.offloaded
sessions_count = args.sessions
files_in_session = args.filespersession
kb_per_file = args.kbperfile

print('Generating list of requests')

clients_and_requests = generate_requests(
    clients_count, 
    requests_per_client, 
    files_in_session, 
    sessions_count)

print('Populating remote sessions')

popoluate_sessions(sessions_count, files_in_session, kb_per_file)

print('Offloading the specified quantity of sessions')

force_offload_url = f"http://{get_ip('k3d-p3')}:31112/function/session-offloading-manager?command=force-offload"
offload_count = int(float(sessions_count) * offloaded_percentage)
for offload in range(offload_count):
    force_offload(force_offload_url, 'session-' + str(offload))

print('Starting clients')

# Start clients and send requests
threads = []

for client in range(clients_count):
    thread = threading.Thread(target=start_client, args=(clients_and_requests['client-' + str(client)],))
    threads.append(thread)
    thread.start()
    print('client-' + str(client) + ' started')

# Stop clients
for thread in threads:
    thread.join()

# Save results
def save_to_csv(title: str, results):
    if not os.path.exists('results'):
       os.makedirs('results')
    filename = 'results/' + title + str(datetime.now(timezone.utc)) + '.csv'
    with open(filename, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['Time','Response Status Code'])
        writer.writerows(results)

save_to_csv('cdn-' + str(offloaded_percentage) + '-', times_and_codes)

# Create histogram
times = [result[0] for result in times_and_codes]
num_bins = 10
n, bins, patches = plt.hist(times, num_bins, facecolor='blue', alpha=0.5)

plt.xlabel('Time (s)')
plt.ylabel('Frequency')
plt.title('Histogram of Request Times')

# Create bar chart of response status code counts
counts = {}
for result in times_and_codes:
    status_code = str(result[1])
    if status_code in counts:
        counts[status_code] += 1
    else:
        counts[status_code] = 1

fig, ax = plt.subplots()
ax.bar(counts.keys(), counts.values())
ax.set_xlabel('Response Status Code')
ax.set_ylabel('Count')
ax.set_title('Count of Response Status Codes')

plt.show()
