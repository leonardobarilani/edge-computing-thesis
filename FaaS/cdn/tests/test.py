import uuid
from connection import Connection
from data_test import DataTest as Data
import threading
import json
import os
import base64

con3 = Connection(node_name='k3d-p3')
con2 = Connection(node_name='k3d-p2')

session = 'session'

file_name_1 = 'file1'
file_name_2 = 'file2'
file_name_3 = 'file3'

file_size_kb_1 = 1024 * 10 # 10 MB
file_size_kb_2 = 1024 * 10 # 10 MB
file_size_kb_3 = 1024 * 10 # 10 MB

file_data_1 = base64.b64encode(os.urandom(file_size_kb_1 * 1024)).decode('utf-8')
file_data_2 = base64.b64encode(os.urandom(file_size_kb_2 * 1024)).decode('utf-8')
file_data_3 = base64.b64encode(os.urandom(file_size_kb_3 * 1024)).decode('utf-8')

file_payload_1 = json.dumps({'fileName':file_name_1,'fileData':file_data_1})
file_payload_2 = json.dumps({'fileName':file_name_2,'fileData':file_data_2})
file_payload_3 = json.dumps({'fileName':file_name_3,'fileData':file_data_3})

def send_file(payload: str) -> int:
	return con3.post('cdn-upload', payload, 
		headers={'X-session':session, 'X-session-request-id':str(uuid.uuid4())})[1]

def get_file(result_dict: dict, file_name: str) -> dict:
	result = con3.get('cdn-download?file=' + file_name,
		headers={'X-session':session, 'X-session-request-id':str(uuid.uuid4())})
	result_dict[file_name] = result

if __name__ == "__main__":
	assert 200 == send_file(file_payload_1)
	assert 200 == send_file(file_payload_2)
	assert 200 == send_file(file_payload_3)

	result_dict = {}
	thread1 = threading.Thread(target=get_file, args=(result_dict, file_name_1))
	thread2 = threading.Thread(target=get_file, args=(result_dict, file_name_2))
	thread3 = threading.Thread(target=get_file, args=(result_dict, file_name_3))
	
	thread1.start()
	thread2.start()
	thread3.start()

	thread1.join()
	thread2.join()
	thread3.join()

	result1 = result_dict[file_name_1]
	result2 = result_dict[file_name_2]
	result3 = result_dict[file_name_3]

	assert result1[1] == 200
	assert result2[1] == 200
	assert result3[1] == 200

	assert result1[0] == file_data_1
	assert result2[0] == file_data_2
	assert result3[0] == file_data_3
