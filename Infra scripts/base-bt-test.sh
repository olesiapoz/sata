#! /bin/bash
for i in {4..1}
do
    echo "Testing replicas N=$i"
    (kubectl scale --replicas=$i -f ../demo1/src/main/kube/deployment.yml)
    wait
    sleep 60

    date +"%T"
    echo "Starting Jmeter test"
    ( cd ../Workloads_prom/phd && ./rBase-bt.sh )
    echo $?
    wait
    date +"%T"
    echo "Finished Jmeter test"
    
    sleep 300
done
