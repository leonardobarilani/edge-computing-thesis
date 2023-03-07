import concurrent
import time
import random
from concurrent.futures import ThreadPoolExecutor
from connection import Connection

random.seed(1234)
sessions_count = 10
products_count = 20 # per session
offloads_count = 5 # per session
threads_count = sessions_count
delay_between_products_ms = 50 # time to wait if a product fails, before retrying
delay_between_offloads_ms = 50 # time to wait if an offload fails, before retrying

con3 = Connection(node_name='k3d-p3')
con2 = Connection(node_name='k3d-p2')

# ------------- 1/5 - Build sessions' requests -------------
requests = []
for product in range(0, products_count):
    for session in range(0, sessions_count):
        requests.append({'session': "session-" + str(session), 'product': "product-" + str(session) + "-" + str(product)})
for session in range(0, sessions_count):
    requests.append({'session': "session-" + str(session)})
random.shuffle(requests)

# ------------- 2/5 - Send requests to populate sessions -------------
def request(req: dict) -> str:
    if 'product' in req:
        res = 0
        while res != 200:
            time.sleep(delay_between_products_ms / 1000)
            res = con3.get('shopping-cart?product=' + req['product'], headers={'X-session': req['session']})[1]
    else:
        for _ in range(0, offloads_count):
            res = 0
            while res != 200:
                time.sleep(delay_between_offloads_ms / 1000)
                res = con3.get('session-offloading-manager?command=force-offload', 
                    headers={'X-forced-session':req['session']})[1]
            ms = random.randrange(50, 200)
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

# ------------- 3/5 - Retrieve data -------------
carts = []
for session in range(0, sessions_count):
    cart = con3.get('session-offloading-manager?command=test-function&type=sessionData&value=session-' + str(session))[0] + con2.get('session-offloading-manager?command=test-function&type=sessionData&value=session-' + str(session))[0]
    carts.append(cart)

# ------------- 4/5 - Assert no data has been lost -------------
for session in range(0, sessions_count):
    for product in range(0, products_count):
        tested_value = '\\"product-' + str(session) + '-' + str(product) + '\\"'
        print("Tested value: " + tested_value)
        assert carts[session].count(tested_value) > 0
print("No data has been lost")


# WARNING - this still doesn't test for client centrict consistency
duplicated_data = False
# ------------- 5/5 - Check that no data has been duplicated (client centrict consistency successfull) -------------
for session in range(0, sessions_count):
    for product in range(0, products_count):
        tested_value = '\\"product-' + str(session) + '-' + str(product) + '\\"'
        print("Tested value: " + tested_value)
        if carts[session].count(tested_value) != 1:
            duplicated_data = True
            print("^^^ Duplicated value ^^^")
if duplicated_data:
    print("Found some duplicated values")
else:
    print("No duplicated values found")
