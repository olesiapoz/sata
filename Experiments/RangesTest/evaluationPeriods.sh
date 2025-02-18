#!/bin/bash

#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 10*90 (3*ThresholdUpdate)
#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
#scenarios ThresholdUpdate period 8*90, PeriodsForEvaluation 10*90 (6*ThresholdUpdate)
#scenarios ThresholdUpdate period 8*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
date

#build demo1 image with such settings
#demo1_label='16k-8k' - original range tests
demo1_label='12k-8k'
#simulation='ComputerDatabaseSimulationOpo'
simulation='ComputerDatabaseSimulationOpoTest'
#itterator='./rangeTestIterator.sh'
itterator='./rangeTestIteratorTests.sh'

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

#declare -a arrOfExperiments=('4x90x20x90' '8x90x20x90' '4x90x10x90'  )
#declare -a arrOfExperiments=('8x90x10x90' '4x90x10x90'  )
#declare -a arrOfExperiments=('4x90x10x90'  )
declare -a arrOfExperiments=('SMAx4x90x10x90'  )
arr_size=${#arrOfExperiments[@]}
echo $arr_size

for ((i = 0; i < (arr_size); i++)); do
  #deploy new autoscaler 4x90x10x90
  date
  echo "Experiment Eval periods settings: $demo1_label "  ${arrOfExperiments[i]} " running simulation $simulation"
   (  kubectl apply -f hpa.yml) &&
 sleep 10
  ( cd ../../autoscaler/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 10
 

  #scale down demo1 to 1
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 60

  kubectl apply -f deployment${arrOfExperiments[i]}.yml &&
  sleep 60
  #collect logs by label app: autoscaler
  (kubectl logs -f -l app=autoscaler > "logs/"${arrOfExperiments[i]}.log) &
  #run gatling
  #(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./iterator.sh) &&
  #(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./gatling.sh -s  ${simulation} -rm local -rd "dyn50" ) &&
  (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ${itterator} ) &&

  #delete autoscaler
  sleep 3
  kill $!

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

cp nohup.out logs/nohup.out