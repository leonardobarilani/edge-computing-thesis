#!/bin/sh
if [ $# -eq 0 ]
then
    echo One argument needed: profilename
    exit 1
fi

kubectl config use-context $1
printf "\n"


docker build -t k3d-docker-io.localhost:5000/proxy:latest .
docker push k3d-docker-io.localhost:5000/proxy:latest
kubectl apply -f proxy-deployment.yaml

#!/bin/bash

# Get the external IP of the service
external_ip=$(kubectl get service proxy-service -o=jsonpath='{.status.loadBalancer.ingress[0].ip}')

# Check if the external IP is available
if [[ -n "$external_ip" ]]; then
    echo "External IP: $external_ip"
    url="http://$external_ip/?service=empty-function"

    # Open the browser with the URL
    if [[ "$(expr substr $(uname -s) 1 5)" == "Linux" ]]; then
        xdg-open "$url"  # Linux
    else
        echo "Unsupported operating system. Please open the browser manually and navigate to:"
        echo "$url"
    fi
else
    echo "External IP not available yet. Please wait..."
fi
