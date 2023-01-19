kubectl port-forward deployment/prometheus 9090:9090 -n openfaas &
xdg-open http://127.0.0.1:9090/alerts?search=