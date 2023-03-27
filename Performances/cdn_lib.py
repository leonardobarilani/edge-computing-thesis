import time
import requests
import os
from datetime import datetime, timezone
import uuid
import random
import string
import threading
import json
from functools import cache

rd = random.Random()
rd.seed(20230503)
uuid.UUID(int=rd.getrandbits(128))

def generate_requests(
    clients_count: int, 
    requests_per_client: int, 
    files_in_session: int, 
    sessions_count: int) -> dict:

    clients_and_requests = {}
    for client in range(0, clients_count):
        requests_list = []
        for request in range(0, requests_per_client):
            req = {}
            req['uuid'] = uuid.uuid4()
            req['file'] = 'file-' + str(random.randint(0,files_in_session - 1))
            req['session'] = 'session-' + str(random.randint(0,sessions_count - 1))
            requests_list.append(req)
        clients_and_requests['client-' + str(client)] = requests_list
    return clients_and_requests

@cache
def get_ip(node_name: str):
    ip_command = 'kubectl config use-context ' + node_name + ' > /dev/null && kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"'
    return os.popen(ip_command).read().translate(str.maketrans('', '', ' \n\t\r'))

times_and_codes = []

def get_file(gateway: str, session: str, file_name: str, uuid: str):
    response = None
    
    while(True):
        start_time = time.monotonic()
        response = requests.get(gateway + 'cdn-download?file=' + file_name,
            headers={'X-session':session, 'X-session-request-id':str(uuid)})
        end_time = time.monotonic()
        time_taken = end_time - start_time
        code = response.status_code
        times_and_codes.append((time_taken, response.status_code))

        if response.status_code == 200 or response.status_code == 208:
            break
        else:
            time.sleep(1)
    return response

def send_file(gateway: str, session: str, file: str, kb_per_file: int):
    response = None
    headers = {'X-session':session,'X-session-request-id':str(uuid.uuid4())}
    file_data = ''.join(random.choices(string.ascii_lowercase, k=1024 * kb_per_file))
    payload = json.dumps({'fileName':file,'fileData':file_data})

    while(True):
        response = requests.post(
            gateway + 'cdn-upload', 
            payload, 
            headers={
                'X-session':session, 
                'X-session-request-id':str(uuid.uuid4())})

        if response.status_code == 200 or response.status_code == 208:
            break
        else:
            time.sleep(1)

def popoluate_sessions(sessions_count: int, files_in_session: int, kb_per_file: int):
    ip = get_ip('k3d-p3')
    for session in range(sessions_count):
        for file in range(files_in_session):
            print(f"Sending {'file-' + str(file)} in {'session-' + str(session)}")
            send_file(
                f"http://{ip}:31112/function/",
                'session-' + str(session),
                'file-' + str(file),
                kb_per_file)

def force_offload(url: str, session: str):
    headers = {'X-forced-session':session}

    while(True):
        print(f"Offloading {url}")
        response = requests.get(url, headers=headers)
        code = response.status_code
        if response.status_code == 200:
            break
        else:
            time.sleep(1)

def start_client(requests_list: list):
    ip = get_ip('k3d-p3')
    for req in requests_list:
        print(f"Getting {req}")
        get_file(
            f"http://{ip}:31112/function/",
            req['session'],
            req['file'],
            req['uuid'])
