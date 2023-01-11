
if [ $# -ne 2 ]
then
    echo 'Two arguments needed: image-name image-version'
    exit 1
fi

docker tag $1:latest k3d-docker-io:5000/$1:$2
docker push k3d-docker-io:5000/$1:$2
