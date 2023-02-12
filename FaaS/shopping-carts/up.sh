echo building and pushing to local register
faas-cli up --skip-deploy

echo Deploying
./deploy.sh || exit 1

echo Testing
read -p "Press enter to start tests"
./tests/run-tests.sh || exit 1
