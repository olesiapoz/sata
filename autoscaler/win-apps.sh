#! /bin/bash

mvn clean install -P docker

wait
echo "docker file build"

docker push olesiapoz/autoscaler:latest
wait 
echo "Autoscaler pushed"
sleep 3
( cd src/main/kube &&  kubectl delete -f deployment.yml -f service.yml -f autoscalerRBAC.yaml)
( cd src/main/kube &&  kubectl apply -f deployment.yml -f service.yml -f autoscalerRBAC.yaml)
wait
echo "Autoscaler Deployed"
