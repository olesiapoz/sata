sudo mvn clean install -P docker
wait

label='12k-8k'

echo "docker file build"
sudo docker image tag  olesiapoz/demo1:latest olesiapoz/demo1:${label}
sudo docker push olesiapoz/demo1:${label}

#sudo docker push olesiapoz/demo1:latest
wait

echo "docker file build and pushed"

