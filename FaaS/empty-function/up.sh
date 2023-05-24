echo building and pushing to local register
cd ../
faas-cli up --skip-deploy --parallel 1 --filter empty-function || exit 1

echo Deploying
./empty-function/deploy.sh || exit 1
