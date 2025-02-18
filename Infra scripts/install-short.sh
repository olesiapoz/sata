#! /bin/bash
echo "Removing CPA"
( cd ../custom-pod-autoscaler && ./removeCpa.sh )
wait
sleep 30
kubectl delete hpa demo1-hpa
sleep 30
echo "Installing CPA"
( cd ../custom-pod-autoscaler && ./cpa.sh )
wait
sleep 60
echo "Installing Autoscaler"
( cd ../autoscaler && ./apps.sh )
sleep 50