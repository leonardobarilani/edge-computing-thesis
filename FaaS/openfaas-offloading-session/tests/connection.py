import requests


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
    def __init__(self, node_name: str, ip):
        self._node_name = node_name
        self._ip = ip

    def post(self, openfaas_fn: str, data: str, headers: dict = {}):
        response = requests.post('http://' + self._ip + ':31112/function/' + openfaas_fn, headers=headers, data=data)
        print(bcolors.OKCYAN + self._node_name + "  " + openfaas_fn + " response: \n" + bcolors.OKGREEN + str(
            response.content, "utf-8") + bcolors.ENDC + "\n")
        return str(response.content, "utf-8")

    def get(self, openfaas_fn: str, headers: dict = {}):
        response = requests.get('http://' + self._ip + ':31112/function/' + openfaas_fn, headers=headers)
        print(bcolors.OKCYAN + self._node_name + "  " + openfaas_fn + " response: \n" + bcolors.OKGREEN + str(
            response.content, "utf-8") + bcolors.ENDC + "\n")
        return str(response.content, "utf-8")
