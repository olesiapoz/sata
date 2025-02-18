#! /bin/bash

sudo mvn clean install -P docker
#label='SAAx10x60x34x25RPS'
#label='SAAx10x60x27x25RPS'
#label='HPATracking'
#label='SMAx4x90x20x90'
#label=SMAx4x90x20x90v2
label=SMAx4x90x40x90v2

wait
echo "docker file build"
sudo docker image tag olesiapoz/autoscaler:latest olesiapoz/autoscaler:${label}
sudo docker push olesiapoz/autoscaler:${label}
#sudo docker push olesiapoz/autoscaler:latest
wait 
echo "Autoscaler pushed"

