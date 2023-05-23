import time
import requests
import csv
import os
import argparse
import uuid
import matplotlib.pyplot as plt
import numpy as np
from datetime import datetime, timezone

parser = argparse.ArgumentParser(description='Make requests to a server and save results to a CSV file.')
parser.add_argument('--count', type=int, default=100, help='Number of requests to make')
args = parser.parse_args()

count = args.count

session = 'marco'

def get_ip(node_name: str):
    ip_command = 'kubectl config use-context ' + node_name + ' > /dev/null && kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"'
    return os.popen(ip_command).read().translate(str.maketrans('', '', ' \n\t\r'))

def make_request(url: str):
    response = None
    headers = {'X-session':session,'X-session-request-id':str(uuid.uuid4()),'X-forced-session':session}

    for i in range(3):
        response = requests.get(url, headers=headers)
        if response.status_code == 200 or response.status_code == 208:
            break
    if response.history:
        print("Request was redirected:")
        for resp in response.history:
            print(resp.status_code, resp.url)
        print()
    return response

def save_to_csv(title: str, results):
    if not os.path.exists('results'):
       os.makedirs('results')
    filename = 'results/' + title + str(datetime.now(timezone.utc)) + '.csv'
    with open(filename, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['Time', 'Response Code'])
        for result in results:
            writer.writerow(result)

times_and_codes = []  # a list to hold the time and response code of each request

empty_function = f"http://{get_ip('k3d-p3')}:31112/function/empty-function"
force_offload = f"http://{get_ip('k3d-p3')}:31112/function/session-offloading-manager?command=force-offload"
make_request(empty_function)
make_request(force_offload)

for i in range(count):
    print(str(i))
    # make the request and record the start time
    start_time = time.monotonic()
    response = make_request(empty_function)
    end_time = time.monotonic()

    # calculate the time the request took and record the time and response code
    time_taken = end_time - start_time
    code = response.status_code
    times_and_codes.append((time_taken, code))

save_to_csv('average_response_time_redirect', times_and_codes)

# Create histogram
times = [result[0] for result in times_and_codes]
num_bins = 10
n, bins, patches = plt.hist(times, num_bins, facecolor='blue', alpha=0.5)

avg_time = sum(times) / len(times)
plt.axvline(avg_time, color='r', linestyle='dashed', linewidth=1)
plt.xlabel(f"Time (s) (Average: {avg_time:.3f})")
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
