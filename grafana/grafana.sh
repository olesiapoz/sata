# !\bin\bash

helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

kubectl apply -f templates/configMap.yaml

echo "Installing grafana on port 3000"

wait
set -e

helm upgrade --install grafana -f values.yaml grafana/grafana --version=6.5.0

wait 

echo "Grafana install is finished"
