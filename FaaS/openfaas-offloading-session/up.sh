echo building and pushing to local register
faas-cli up --skip-deploy

echo Deploying
./deploy.sh || exit 1

echo Testing
./tests/run-tests.sh || exit 1

echo Fetching logs
./logs.sh k3d-p3 || exit 1
./logs.sh k3d-p2 || exit 1
