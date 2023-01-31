import requests
import os

class bcolors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'

class Connection:
	def __init__(self, node_name: str, session: str='marco', auth: (str, str)=('admin', 'password')):
		self._session = session
		self._node_name = node_name

		self._ip_command = 'kubectl config use-context ' + node_name + ' > /dev/null && kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"'
		self._ip = os.popen(self._ip_command).read().translate(str.maketrans('', '', ' \n\t\r'))
		self._auth = auth

	def post(self, openfaas_fn: str, data: str, extra_headers: dict={}):
		extra_headers['X-session'] = self._session
		self._session = requests.post('http://' + self._ip + ':31112/function/' + openfaas_fn, auth=self._auth, headers=extra_headers, data=data)
		print (bcolors.OKCYAN + self._node_name + "  " + openfaas_fn + " response: \n" + bcolors.OKGREEN + str(response.content, "utf-8") + bcolors.ENDC + "\n")
		return str(self._session.content, "utf-8")
		
	def get(self, openfaas_fn: str, extra_headers: dict={}):
		extra_headers['X-session'] = self._session
		response = requests.get('http://' + self._ip + ':31112/function/' + openfaas_fn, auth=self._auth, headers=extra_headers)
		print (bcolors.OKCYAN + self._node_name + "  " + openfaas_fn + " response: \n" + bcolors.OKGREEN + str(response.content, "utf-8") + bcolors.ENDC + "\n")
		return str(response.content, "utf-8")
