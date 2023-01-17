echo Building
faas-cli build || exit 1

echo Pushing
$SCRIPTS_PATH/local-push.sh carrone/session-offloading-manager latest && sleep 5 || exit 1

echo Deploying
./deploy.sh || exit 1

echo Testing
./tests/run-tests.sh || exit 1

echo Fetching logs
./logs.sh k3d-p3 || exit 1
./logs.sh k3d-p2 || exit 1
