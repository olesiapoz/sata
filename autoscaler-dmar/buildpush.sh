#! /bin/bash

sudo mvn clean install -P docker
#label='SAAx10x60x34x25RPS'
#label='SAAx10x60x27x25RPS'
label=SAA

wait
echo "docker file build"
sudo docker image tag olesiapoz/autoscaler:latest olesiapoz/autoscaler:${label}
sudo docker push olesiapoz/autoscaler:${label}
#sudo docker push olesiapoz/autoscaler:latest
wait 
echo "Autoscaler pushed"

