echo building and pushing to local register
cd ../
faas-cli up --skip-deploy --parallel 1 --filter shopping-cart  || exit 1

./shopping-carts/deploy.sh || exit 1

echo Testing
read -p "Press enter to start tests"
./shopping-carts/tests/run-tests.sh || exit 1
