import base64
import subprocess
import requests
from urllib.parse import urlparse, parse_qs
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
import uuid

class ProxyHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        ip_address = get_cluster_ip()
        parsed_url = urlparse(self.path)
        query_params = parse_qs(parsed_url.query)

        # Reconstruct the proxy URL with query parameters
        url = f'http://{ip_address}:31112/function/cdn-download{parsed_url.path}'
        if query_params:
            query_string = '&'.join([f'{key}={value[0]}' for key, value in query_params.items()])
            url += f'?{query_string}'

        headers = {
            'X-session': 'sessionnnn',
            'X-session-request-id': str(uuid.uuid4()),
        }

        response = requests.get(url, headers=headers)

        if response.status_code == 200:
            decoded_file = base64.b64decode(response.content)

            self.send_response(200)
            self.send_header('Content-type', 'video/mp4')
            self.end_headers()
            self.wfile.write(decoded_file)
        else:
            self.send_response(response.status_code)
            self.send_header('Content-type', 'text/plain')
            self.end_headers()
            self.wfile.write(response.text.encode('utf-8'))

def get_cluster_ip():
    config = 'k3d-p3'
    cmd = ['kubectl', '--context', config, 'get', 'nodes', '-o', 'jsonpath="{.items[0].status.addresses[0].address}"']
    output = subprocess.check_output(cmd, universal_newlines=True)
    ip_address = output.strip('"')
    return ip_address

if __name__ == '__main__':
    HOST = 'localhost'
    PORT = 8000

    server = ThreadingHTTPServer((HOST, PORT), ProxyHandler)
    print(f'Server running on {HOST}:{PORT}')
    server.serve_forever()
