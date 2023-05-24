import uuid
from connection import Connection
import base64

con3 = Connection(node_name='k3d-p3')

session = 'sessionnnn'
file_path = "../very_serious_video.mp4"

if __name__ == "__main__":
    print("before")

    with open(file_path, "rb") as file:
        original_bytes = file.read()

    encoded_string = base64.b64encode(original_bytes).decode('utf-8')

    assert 200 == con3.post('cdn-upload', encoded_string,
                            headers={'X-session': session, 'X-session-request-id': str(uuid.uuid4())})[1]

    print("after")
    result = con3.get('cdn-download?file=' + "mp4",
                          headers={'X-session': session, 'X-session-request-id': str(uuid.uuid4())})

    assert result[1] == 200


    with open(file_path, 'rb') as file:
        assert result[0] == encoded_string

    decoded_bytes = base64.b64decode(result[0])
    assert decoded_bytes == original_bytes
    assert result[2].get("Access-Control-Allow-Origin") == "*"

    print("OK")
    print("try to start proxy.py and open the URL http://127.0.0.1:8000/?file=mp4")

