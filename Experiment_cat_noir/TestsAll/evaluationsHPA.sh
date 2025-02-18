#!/bin/bash

#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 10*90 (3*ThresholdUpdate)
#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
#scenarios ThresholdUpdate period 8*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
date


SAA='HPATrackerx0.4RT'
simulation='ComputerDatabaseSimulationOpo for HPA ' 
echo "Running "${simulation}" simulation"

date
(cd ../../demo1/ &&  ./removeApps.sh) &&
(cd ../../cat_noir/ &&  ./removeApps.sh) &&
sleep 60
declare -a arrOfSimulations=( 'iteratorWC_cat' 'iteratorEDGAR_cat')
sim_arr_size=${#arrOfSimulations[@]}
echo $sim_arr_size

declare -a arrOfHPA=( 'hpa69' 'hpa42')
hpa_arr_size=${#arrOfHPA[@]}
echo $hpa_arr_size

#(cd ../../demo1/ && sudo ./apps${demo1_label}.sh) &&
#sleep 75
n=0
for ((n = 0; n < (sim_arr_size); n++)); do
echo "HPA "${arrOfHPA[n]} 

( cd ../../cat_noir/kube &&  kubectl apply  -f deployment.yml -f service.yml -f service-monitor.yml) &&

sleep 30
echo "Demo1 Cat Noir is deployed"


( cd ../../autoscaler/src/main/kube &&  kubectl delete -f deployment.yml -f service.yml -f autoscalerRBAC.yaml) &&
( cd ../../prod/HPAs &&  kubectl delete -f hpa.yml) &&
#sleep 75

  for ((z = 0; z < (hpa_arr_size); z++)); do
    echo "HPA "${arrOfHPA[z]} " removing"
    ( kubectl delete -f ../deployments/deployment${arrOfHPA[z]}.yml) &&
    echo "HPA "${arrOfHPA[z]} " is removed"
  done

sleep 30

  ( cd ../../autoscaler/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 30
 
  #scale down demo1 to 1
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 10

  kubectl apply -f ../deployments/deployment${SAA}.yml &&
  kubectl apply -f ../deployments/${arrOfHPA[n]}.yml &&
  #sleep 60
  echo "1"
  sleep 5
  #collect logs by label app: autoscaler
  (kubectl logs -f -l app=autoscaler > logs/${arrOfHPA[n]}${arrOfSimulations[n]}.log) &
  echo "Storing los to " logs/${arrOfHPA[n]}${arrOfSimulations[n]}.log} 

  sleep 5
  
  echo "Gatling fired"
  (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ &&  "./${arrOfSimulations[n]}.sh" ) &&
  echo "Gatling stopped"

  #delete autoscaler
  sleep 3
  kill $!

  kubectl delete -f ../deployments/deployment${SAA}.yml &&
  kubectl delete -f ../deployments/${arrOfHPA[n]}.yml &&
  wait
  sleep 5


  ( cd ../../autoscaler/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 60

echo "Demo1 is removing"


( cd ../../cat_noir/kube &&  kubectl delete  -f deployment.yml -f service.yml -f service-monitor.yml) &&
echo "Demo1 is removed"
done


cp nohup.out logs/nohup.out
