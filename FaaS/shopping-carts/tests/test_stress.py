import uuid
import concurrent
from concurrent.futures import ThreadPoolExecutor
from connection import Connection

sessions_count = 5
products_count = 10
threads = 10

con3 = Connection(node_name='k3d-p3')

# ------------- 1/3 - Build sessions' requests -------------
requests = []
for product in range(0, products_count):
    for session in range(0, sessions_count):
        requests.append({"session": "session-" + str(session), "product": "product-" + str(session) + "-" + str(product)})

# ------------- 2/3 - Send requests to populate sessions -------------
def request(req: dict) -> str:
    return con3.get('shopping-cart?product=' + req["product"], 
        headers={'X-session': req["session"],'X-session-request-id':str(uuid.uuid4())})[0]

with ThreadPoolExecutor(max_workers=threads) as executor:
    future_to_url = {executor.submit(request, req) for req in requests}
    print("type check: " + str(type(future_to_url)))
    for future in concurrent.futures.as_completed(future_to_url):
        try:
            data = future.result()
        except Exception as e:
            print('Exception raised while resolving the future: ', e)

# ------------- 3/3 - Assert correctness of sessions -------------
for session in range(0, sessions_count):
    cart = con3.get('session-offloading-manager?command=test-function&type=sessionData&value=session-' + str(session))[0]
    for product in range(0, products_count):
        assert '\\"product-' + str(session) + '-' + str(product) + '\\"' in cart
