echo "Starting minikube ...";
minikube start --vm-driver=xhyve
echo "Started minikube";

echo "Setting the docker machine env"
eval $(minikube docker-env)
echo "Docket env set"

echo "Setting kubectl context"
kubectl config set-context minikube --namespace=message-hub
echo "Set kubectl context"
