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
sleep 50
echo "Installing HPA"
( cd ../prod/HPAs && kubectl apply -f hpa.yml )
sleep 15
echo "Ready"

#export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=grafana,app.kubernetes.io/instance=grafana" -o jsonpath="{.items[0].metadata.name}")
 #    kubectl --namespace default port-forward $POD_NAME 3000

#wait
#sleep 240
#nohup ./runtests.sh


