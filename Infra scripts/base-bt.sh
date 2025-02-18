#! /bin/bash
while true;
do
    date +"%T"
    echo "Starting Jmeter test"
    ( cd ../Workloads_prom/phd && ./base-bt.sh )
    echo $?
    wait
    date +"%T"
    echo "Finished Jmeter test"
    
    sleep 90
done
