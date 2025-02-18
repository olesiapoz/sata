#! /bin/bash

sudo mvn clean install -P docker
#label='SAAx10x60x27x25RPS''
label='SAAx10x60x55x25RPSx0.4RT_mid75'

wait
echo "docker file build"
sudo docker image tag olesiapoz/autoscaler:latest olesiapoz/autoscaler:${label}
sudo docker push olesiapoz/autoscaler:${label}
#sudo docker push olesiapoz/autoscaler:latest
wait 
echo "Autoscaler pushed"
sleep 3
( cd src/main/kube &&  kubectl delete -f deployment.yml -f service.yml -f autoscalerRBAC.yaml)
sleep 10
( cd src/main/kube &&  kubectl apply -f deployment.yml -f service.yml -f autoscalerRBAC.yaml)
wait
echo "Autoscaler Deployed"
