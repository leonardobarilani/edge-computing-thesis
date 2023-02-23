import concurrent
import time
import random
from concurrent.futures import ThreadPoolExecutor
from connection import Connection

random.seed(1234)
sessions_count = 5
products_count = 10 # per session
offloads_count = 5 # per session
threads_count = (products_count + offloads_count) * sessions_count

con3 = Connection(node_name='k3d-p3')

# ------------- 1/3 - Build sessions' requests -------------
requests = []
for product in range(0, products_count):
    for session in range(0, sessions_count):
        requests.append({'session': "session-" + str(session), 'product': "product-" + str(session) + "-" + str(product)})
for session in range(0, sessions_count):
    requests.append({'session': "session-" + str(session)})
random.shuffle(requests)

# ------------- 2/3 - Send requests to populate sessions -------------
def request(req: dict) -> str:
    if 'product' in req:
        res = "..."
        while res[:1] != '[':
            time.sleep(0.05)
            res = con3.get('shopping-cart?product=' + req['product'], headers={'X-session': req['session']})
    else:
        for _ in range(0, offloads_count):
            res = "..................."
            while res[:11] != 'Offloading:':
                time.sleep(0.05)
                res = con3.get('session-offloading-manager?command=force-offload', 
                    headers={'X-forced-session':req['session']})
            ms = random.randrange(100, 1000)
            time.sleep(ms / 1000)
            con3.get('session-offloading-manager?command=force-onload')

for session in range(0, sessions_count):
    con3.get('shopping-cart?product=init', headers={'X-session': "session-" + str(session)})

print("Starting " + str(threads_count) + " threads")
with ThreadPoolExecutor(max_workers=threads_count) as executor:
    futures = {executor.submit(request, req) for req in requests}
    for future in concurrent.futures.as_completed(futures):
        try:
            future.result()
        except Exception as e:
            print('Exception raised while resolving the future: ', e)

# ------------- 3/3 - Assert correctness of sessions -------------
for session in range(0, sessions_count):
    cart = con3.get('session-offloading-manager?command=test-function&type=sessionData&value=session-' + str(session))
    for product in range(0, products_count):
        tested_value = 'product-' + str(session) + '-' + str(product)
        print("Tested value: " + tested_value)
        assert cart.count(tested_value) == 1
