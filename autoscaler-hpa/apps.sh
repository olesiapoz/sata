#! /bin/bash

sudo mvn clean install -P docker
#label='4x90x20x90'
#label='4x90x10x90'
#label='6x90x20x90'
#label='6x90x20x90'
#label='SMAx4x90x10x90'
#label='SMAx4x90x20x90'
#label='SMAx4x90x20x90'
#label='SMAx8x90x20x90'
#label='SAAx10x60x34'
#label='SMAx6x90x20x90x0.4RT'
#label='SMAx4x90x10x90x0.4RT'
label='HPATrackerx0.4RT'

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
