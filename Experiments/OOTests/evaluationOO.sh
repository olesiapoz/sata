#!/bin/bash

#OO tests SAAx10x60x27x25RPS 'avgRTx4x90x10x90', '4x90x10x90' '12k-8k'

echo "OO tests SAAx10x60x27x25RPS avgRTx4x90x10x90, 4x90x10x90 12k-8k"

simulation='SwitchOnOffSimulationOpo'
#simulation='SwitchOnOffSimulationTesting'
echo "Running "${simulation}" simulation"


#declare -a arrOfExperiments=('4x90x20x90' '8x90x20x90' '4x90x10x90'  )
declare -a arrOfExperiments=('avgRTx4x90x10x90' '4x90x10x90'  )
arr_size=${#arrOfExperiments[@]}
echo $arr_size
#build demo1 image with such settings
demo1_label='12k-8k'
SAA_label='SAAx10x60x27x25RPS'

date

(cd ../../demo1/ &&  ./removeApps.sh) &&
sleep 60

#(cd ../../demo1/ && sudo ./apps${demo1_label}.sh) &&
#sleep 75


( cd ../../demo1/src/main/kube &&  kubectl apply  -f service.yml -f service-monitor.yml)
wait

kubectl apply -f deploymentDemo1x${demo1_label}.yml &&
echo "Demo1 is deployed"


( cd ../../autoscaler/src/main/kube &&  kubectl delete -f deployment.yml -f service.yml -f autoscalerRBAC.yaml) &&
( cd ../../prod/HPAs &&  kubectl delete -f hpa.yml) &&
sleep 75



for ((i = 0; i < (arr_size); i++)); do
  #deploy new autoscaler 4x90x10x90
  date
   (  kubectl apply -f hpa.yml) &&
 sleep 10
  ( cd ../../autoscaler/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 10
 

  #scale down demo1 to 1
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 60

  echo "deploying" ${arrOfExperiments[i]}

  kubectl apply -f deployment${arrOfExperiments[i]}.yml &&
  sleep 60
  #collect logs by label app: autoscaler
  (kubectl logs -f -l app=autoscaler > "logs/"${arrOfExperiments[i]}.log) &
  #run gatling
  echo "Gatling starting"
  #(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./iterator.sh) &&
  (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./gatling.sh -s  ${simulation} -rm local -rd "dyn50" ) &&
  #(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./rangeTestIterator.sh ) &&

  #delete autoscaler
  sleep 3
  kill $!

 (kubectl logs -l app=autoscaler > "logs/"${arrOfExperiments[i]}v2.log) &&
 sleep 60

  kubectl delete -f deployment${arrOfExperiments[i]}.yml &&
  sleep 5

  ( cd ../../autoscaler/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 10

  #update hpa to initial values
  (  kubectl apply -f hpa.yml) &&

  #scale down demo1 to 1
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 60

  kubectl scale deployment demo1 --replicas=1 &&
  sleep 60
  #run the script once again

done


## SAA test
echo "deleting HPA"
( kubectl delete -f hpa.yml) &&

sleep 30

( cd ../../autoscaler/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
sleep 30
 

  #scale down demo1 to 1
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 90

  echo "Installing CPA"
  ( cd ../../custom-pod-autoscaler && ./cpa.sh )
  wait
  sleep 80

  echo "instaling "${SAA_label}
  kubectl apply -f deployment${SAA_label}.yml &&
  sleep 60
  #collect logs by label app: autoscaler
  (kubectl logs -f -l app=autoscaler > "logs/"${SAA_label}.log) &
  echo "Gatling fired"
  #(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./iterator.sh) &&
  (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./gatling.sh -s  ${simulation} -rm local -rd "dyn50" ) &&
  # (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./iteratorWC.sh ) &&
  echo "Gatling stopped"

  #delete autoscaler
  sleep 3
  kill $!
  (kubectl logs  -l app=autoscaler > "logs/"${SAA_label}v2.log) &&
  wait
  sleep 15
  kubectl delete -f deployment${SAA_label}.yml &&
  wait
  sleep 5


  echo "Removing CPA"
( cd ../../custom-pod-autoscaler && ./removeCpa.sh )
 wait

  ( cd ../../autoscaler/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 60

echo "Demo1 is removing"
( cd ../../demo1/src/main/kube &&  kubectl delete -f service.yml -f service-monitor.yml)
wait

kubectl delete -f deploymentDemo1x${demo1_label}.yml &&
echo "Demo1 is removed"

cp nohup.out logs/nohup.out