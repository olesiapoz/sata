#!/bin/bash

#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 10*90 (3*ThresholdUpdate)
#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
#scenarios ThresholdUpdate period 8*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
date
SAA='SMAx6x90x20x90x0.4RT'
simulation='CatNoirLoadTestOpo'
echo "Running "${simulation}" simulation"

date
(cd ../../demo1/ &&  ./removeApps.sh) &&
(cd ../../cat_noir/ &&  ./removeApps.sh) &&
sleep 30

# sleep 30

( cd ../../autoscaler/src/main/kube &&  kubectl delete -f deployment.yml -f service.yml -f autoscalerRBAC.yaml) &&
( cd ../../prod/HPAs &&  kubectl delete -f hpa.yml) &&
sleep 75

# #declare -a arrOfExperiments=('4x90x10x90' 'SMAx4x90x10x90')
 declare -a arrOfExperiments=($SAA )
 arr_size=${#arrOfExperiments[@]}
 echo $arr_size

for ((i = 0; i < (arr_size); i++)); do
  #deploy new autoscaler 4x90x10x90
  date
   (  kubectl apply -f ../deployments/hpa.yml) &&
  sleep 10
  ( cd ../../autoscaler/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 10
  echo "Running deployment"${arrOfExperiments[i]}" deployment"


  kubectl apply -f ../deployments/deployment${arrOfExperiments[i]}.yml &&
  sleep 40
  #collect logs by label app: autoscaler
   (kubectl logs -f -l app=autoscaler > "logs/"${arrOfExperiments[i]}.log) &


  ( cd ../../cat_noir/kube &&  kubectl apply  -f deployment.yml -f service.yml -f service-monitor.yml)

  sleep 30
  echo "Demo1 Cat Noir is deployed"

  kubectl scale deployment demo1 --replicas=1 &&


  echo "Gatling fired"
  (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ &&  ./gatling.sh -s ${simulation} -rm local -rd "Cat Noir Load Test" ) &&
  echo "Gatling stopped"

  sleep 3
  kill $!

  kubectl delete -f ../deployments/deployment${arrOfExperiments[i]}.yml &&
  sleep 5

  ( cd ../../autoscaler/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 10

  #update hpa to initial values
  (  kubectl delete -f ../deployments/hpa.yml) &&

  #scale down demo1 to 1
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 6

  sleep 5
  
done
