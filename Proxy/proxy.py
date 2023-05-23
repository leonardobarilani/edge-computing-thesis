import os
import requests
import uuid
from flask import Flask, request, abort

app = Flask(__name__)

@app.route('/', methods=['GET'])
def proxy_request():
    service_name = request.args.get('service')
    if not service_name:
        abort(400, 'Missing service parameter')

    url = f'http://{service_name}.openfaas-fn.svc.cluster.local:8080'
    headers = {}

    # Remove headers from the query and add them to the request headers
    for header, value in request.args.items():
        if header.lower() != 'service':
            headers[header] = value

    # Set X-session-request-id header with a random UUID4
    headers['X-session-request-id'] = str(uuid.uuid4())

    # Make the request to the service
    try:
        response = requests.request(
            method=request.method,
            url=url,
            headers=headers,
            params=request.args,
            data=request.data,
            stream=True
        )
        return response.content, response.status_code, response.headers.items()
    except requests.exceptions.RequestException as e:
        abort(500, str(e))

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=int(os.environ.get('PORT', 8080)))
