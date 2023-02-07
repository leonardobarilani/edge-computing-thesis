# Run this command to list all available containers in the local registry (https://stackoverflow.com/questions/31251356/how-to-get-a-list-of-images-on-docker-registry-v2)

curl -X GET http://k3d-docker-io.localhost:5000/v2/_catalog
