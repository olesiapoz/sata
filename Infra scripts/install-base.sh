#! /bin/bash
echo "Installing prometheus"
(cd ../Prometheus && ./prometheus.sh)
wait
sleep 40
echo "Installing Grafana"
(cd ../grafana && ./grafana.sh)
wait
sleep 20
echo "Installing Demo1"
( cd ../demo1 && ./apps.sh )
wait
sleep 30
echo "Installing Autoscaler"
( cd ../autoscaler && ./apps.sh )
wait
sleep 60
echo "Deploying replica set"
(kubectl scale --replicas=2 -f ../demo1/src/main/kube/deployment.yml)
