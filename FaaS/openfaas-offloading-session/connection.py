import requests
import os

class Connection:
	def __init__(self, node_name: str, session: str='marco', auth: (str, str)=('admin', 'password')):
		self.session = session

		self.ip_command = 'kubectl config use-context ' + node_name + ' > /dev/null && kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"'
		self.ip = os.popen(self.ip_command).read().translate(str.maketrans('', '', ' \n\t\r'))
		self.auth = auth

	def post(self, openfaas_fn: str, data: str):
		self.session = requests.post('http://' + self.ip + ':31112/function/' + openfaas_fn, auth=self.auth, headers={'X-session': self.session}, data=data)
		print (ip + "  " + openfaas_fn + " response: \n" + str(response.content, "utf-8") + "\n")
		return str(self.session.content, "utf-8")
		
	def get(self, openfaas_fn: str):
		response = requests.get('http://' + self.ip + ':31112/function/' + openfaas_fn, auth=self.auth, headers={'X-session':self.session,'X-forced-session':self.session})
		print (self.ip + "  " + openfaas_fn + " response: \n" + str(response.content, "utf-8") + "\n")
		return str(response.content, "utf-8")
