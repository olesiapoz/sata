#!/bin/bash

#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 10*90 (3*ThresholdUpdate)
#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
#scenarios ThresholdUpdate period 8*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
date
SAA='SAAx9x60x55x27RPSx0.4RT'
simulation='CatNoirLoadTestOpo'
echo "Running "${simulation}" simulation"

date
(cd ../../demo1/ &&  ./removeApps.sh) &&
(cd ../../cat_noir/ &&  ./removeApps.sh) &&
sleep 30

sleep 30

( cd ../../autoscaler-saa/src/main/kube &&  kubectl delete -f deployment.yml -f service.yml -f autoscalerRBAC.yaml) &&
( cd ../../prod/HPAs &&  kubectl delete -f hpa.yml) &&
sleep 25

#declare -a arrOfExperiments=('4x90x10x90' 'SMAx4x90x10x90')
declare -a arrOfExperiments=($SAA )
arr_size=${#arrOfExperiments[@]}
echo $arr_size
echo  "SAA load testing"

  ( cd ../../autoscaler-saa/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 30

  
( cd ../../cat_noir/kube &&  kubectl apply  -f deployment.yml -f service.yml -f service-monitor.yml) &&

echo "Cat_Noir is deployed"

   #scale down demo1 to 10
  kubectl scale deployment demo1 --replicas=10 &&
  sleep 20

  kubectl apply -f ../deployments/deployment${SAA}.yml &&
  echo "Running deployment"${SAA}" deployment"
  sleep 60
  #collect logs by label app: autoscaler
  (kubectl logs -f -l app=autoscaler > "logs/"${SAA}${simulation}.log) &


  sleep 60

   echo "Gatling fired"
(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ &&  ./gatling.sh -s ${simulation} -rm local -rd "Cat Noir Load Test" ) &&
  echo "Gatling stopped"


  sleep 3
  kill $!

  kubectl delete -f ../deployments/deployment${SAA}.yml &&
  echo "Stoping deployment"${SAA}" deployment"
  wait
  sleep 5

  (cd ../../autoscaler-saa/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 60
  (cd ../../cat_noir/ &&  ./removeApps.sh) &&
  echo "Demo1 is removed"





