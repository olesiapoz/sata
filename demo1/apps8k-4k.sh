sudo mvn clean install -P docker
wait

label='8k-4k'

echo "docker file build"
sudo docker image tag  olesiapoz/demo1:latest olesiapoz/demo1:${label}
sudo docker push olesiapoz/demo1:${label}

#sudo docker push olesiapoz/demo1:latest
wait

echo "docker file build and pushed for " ${label}

