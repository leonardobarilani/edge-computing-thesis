echo building and pushing to local register

cd ../
faas-cli up --skip-deploy --filter speech-to-text || exit 1

echo Deploying
./speech-to-text/deploy.sh || exit 1

echo Run the following commands to analyze the logs:
echo ../logs.sh k3d-p3
echo ../logs.sh k3d-p2
echo ../logs.sh k3d-p1
