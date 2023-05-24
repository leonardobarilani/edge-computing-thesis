import base64
import os
import http.server
import socketserver
import webbrowser
import requests
import uuid

# Define the URL and headers
url = "http://172.18.0.4:31112/function/cdn-download?file=mp4"
headers = {
    "X-session": "sessionnnn",
    "X-session-request-id": str(uuid.uuid4())
}

# Send the GET request and retrieve the file
response = requests.get(url, headers=headers)
file_data = response.content

# Decode the file using base64
decoded_file = base64.b64decode(file_data)

# Save the decoded file to disk
file_path = "downloaded_file.mp4"
with open(file_path, "wb") as file:
    file.write(decoded_file)

# Define a local server to stream the file
PORT = 8000

class MyHttpRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=os.path.dirname(file_path), **kwargs)

# Start the local server
with socketserver.TCPServer(("", PORT), MyHttpRequestHandler) as httpd:
    print(f"Server running on http://localhost:{PORT}")

    # Open a browser to stream the file
    webbrowser.open(f"http://localhost:{PORT}/{os.path.basename(file_path)}")

    # Serve the file indefinitely until interrupted
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass

# Cleanup: Delete the downloaded file
os.remove(file_path)
