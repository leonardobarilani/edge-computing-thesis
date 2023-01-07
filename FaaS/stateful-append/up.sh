echo Building
faas-cli build || exit 1

echo Pushing
$HOME/thesis-scripts/local-push.sh carrone/stateful-append latest || exit 1

echo Deploying
./deploy.sh || exit 1

echo Testing
python3 test.py || exit 1

echo Fetching logs
./logs.sh k3d-p3 || exit 1
