echo building and pushing to local register
faas-cli up --skip-deploy

echo Deploying
./deploy.sh || exit 1
