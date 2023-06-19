docker pull curlimages/curl:8.1.2
docker tag curlimages/curl:8.1.2 k3d-docker-io.localhost:5000/curl:latest
docker push k3d-docker-io.localhost:5000/curl:latest
