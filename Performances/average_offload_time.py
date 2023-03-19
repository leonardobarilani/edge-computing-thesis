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
parser.add_argument('--count', type=int, default=2, help='Number of requests to make')
parser.add_argument('--tests', type=int, default=100, help='Number of tests to make')
args = parser.parse_args()

count = args.count
tests = args.tests

def get_ip(node_name: str):
    ip_command = 'kubectl config use-context ' + node_name + ' > /dev/null && kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"'
    return os.popen(ip_command).read().translate(str.maketrans('', '', ' \n\t\r'))

def make_request(url: str):
    response = None
    headers = {'X-session':'session','X-session-request-id':str(uuid.uuid4()),'X-forced-session':'session'}

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
        writer.writerow(['Time'])
        writer.writerows(results)
        #for result in results:
        #    writer.writerow(result)

times = []  # a list to hold the time of each test

empty_function = f"http://{get_ip('k3d-p3')}:31112/function/empty-function"
force_offload = f"http://{get_ip('k3d-p3')}:31112/function/session-offloading-manager?command=force-offload"
force_onload = f"http://{get_ip('k3d-p3')}:31112/function/session-offloading-manager?command=force-onload"

for test in range(tests):
    # make the test and record the time
    start_time = time.monotonic()
    for req in range(count):
        if req == count / 2:
            print('offloading')
            make_request(force_offload)

        print(str(req))
        response = make_request(empty_function)
    end_time = time.monotonic()

    # return the session for the next test
    print('onloading')
    make_request(force_onload)

    # calculate the time the request took and record it
    time_taken = end_time - start_time
    times.append([time_taken])

save_to_csv('average_offload_times', times)

# Create histogram
plot_times = [result[0] for result in times]
num_bins = 10
n, bins, patches = plt.hist(plot_times, bins=num_bins, facecolor='blue', alpha=0.5)

plt.xlabel('Time (s)')
plt.ylabel('Frequency')
plt.title('Histogram of Request Times')

plt.show()
