echo building and pushing to local register
cd ../
faas-cli up --skip-deploy --parallel 1 --filter example || exit 1

echo Deploying
./cron-function-example/deploy.sh || exit 1
