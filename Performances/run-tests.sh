IP_P3=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p3)
IP_P2=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p2)
IP_P1=$(kubectl get nodes -o jsonpath="{.items[0].status.addresses[0].address}" --context k3d-p1)

python3 average_response_time.py --count 100 --url http://$IP_P3:31112/function/empty-function

