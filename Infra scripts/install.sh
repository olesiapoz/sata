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
echo "Installing CPA"
( cd ../custom-pod-autoscaler && ./cpa.sh )
wait
sleep 30
echo "Installing Autoscaler"
( cd ../autoscaler && ./apps.sh )
sleep 50

