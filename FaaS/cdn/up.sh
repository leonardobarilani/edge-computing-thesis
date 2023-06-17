echo building and pushing to local register
cd ../
faas-cli up --skip-deploy --parallel 2 --filter cdn-* || exit 1

echo Deploying
./cdn/deploy.sh || exit 1
