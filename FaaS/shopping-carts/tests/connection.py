import os
import requests
from retrying import retry


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
    def __init__(self, node_name: str):
        self.__node_name = node_name

        self.__ip_command = 'kubectl config use-context ' + node_name + ' > /dev/null && kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}"'
        self.__ip = os.popen(self.__ip_command).read().translate(str.maketrans('', '', ' \n\t\r'))

    def __retry_if_connection_error(exception):
        print ("Connection error: " + str(ConnectionError))
        return isinstance(exception, ConnectionError)

    @retry(retry_on_exception=__retry_if_connection_error, wait_fixed=50)
    def post(self, openfaas_fn: str, data: str, headers: dict={}):
        response = requests.post('http://' + self.__ip + ':31112/function/' + openfaas_fn, headers=headers, data=data)
        print (bcolors.OKCYAN + self.__node_name + "  " + openfaas_fn + " response (" + str(response.status_code) + "): \n" + bcolors.OKGREEN + str(response.content, "utf-8") + bcolors.ENDC)
        if response.history:
            print(bcolors.WARNING + "Request was redirected")
            for resp in response.history:
                print(resp.status_code, resp.url)
            print("Final destination:")
            print(response.status_code, response.url, bcolors.ENDC)
        print()
        return str(response.content, "utf-8")
        
    @retry(retry_on_exception=__retry_if_connection_error, wait_fixed=50)
    def get(self, openfaas_fn: str, headers: dict={}) -> (str, int):
        response = requests.get('http://' + self.__ip + ':31112/function/' + openfaas_fn, headers=headers)
        print (bcolors.OKCYAN + self.__node_name + "  " + openfaas_fn + " response (" + str(response.status_code) + "): \n" + bcolors.OKGREEN + str(response.content, "utf-8") + bcolors.ENDC)
        if response.history:
            print(bcolors.WARNING + "Request was redirected")
            for resp in response.history:
                print(resp.status_code, resp.url)
            print("Final destination:")
            print(response.status_code, response.url, bcolors.ENDC)
        print()
        return str(response.content, "utf-8"), response.status_code
