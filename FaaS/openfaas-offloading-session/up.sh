echo building and pushing to local register
cd ../
faas-cli up --skip-deploy --filter session-offloading-manager || exit 1

echo Deploying
./openfaas-offloading-session/deploy.sh || exit 1

echo Testing
read -p "Press enter to start tests"
./openfaas-offloading-session/tests/run-tests.sh || exit 1

echo Run the following commands to analyze the logs:
echo ../logs.sh k3d-p3
echo ../logs.sh k3d-p2
echo ../logs.sh k3d-p1
