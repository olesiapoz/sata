#!/bin/bash

#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 10*90 (3*ThresholdUpdate)
#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
#scenarios ThresholdUpdate period 8*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
date
simulation='Demo1LoadTestOpo'
SAA='SAAx10x60x34x25RPS'
demo1_label='12k-8k'

echo "Running "${simulation}" simulation"

date
(cd ../../demo1/ &&  ./removeApps.sh) &&
(cd ../../cat_noir/ &&  ./removeApps.sh) &&
sleep 30

sleep 30

( cd ../../autoscaler-saa/src/main/kube &&  kubectl delete -f deployment.yml -f service.yml -f autoscalerRBAC.yaml) &&
( cd ../../prod/HPAs &&  kubectl delete -f hpa.yml) &&
sleep 25

declare -a arrOfExperiments=($SAA )
arr_size=${#arrOfExperiments[@]}
echo $arr_size
echo  "SAA load testing"

  ( cd ../../autoscaler-saa/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 30

  
( cd ../../demo1/src/main/kube &&  kubectl apply   -f service.yml ) &&
sleep 2

kubectl apply -f ../deployments/deploymentDemo1x${demo1_label}.yml &&
sleep 25

echo "Demo1 is deployed"

   #scale down demo1 to 10
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 20

  kubectl apply -f ../deployments/deployment${SAA}.yml &&
  echo "Running deployment"${SAA}" deployment"
  sleep 70
  #collect logs by label app: autoscaler
  (kubectl logs -f -l app=autoscaler > "logs/"${SAA}${simulation}.log) &

  sleep 10

   echo "Gatling fired"
(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ &&  ./gatling.sh -s ${simulation} -rm local -rd "demo1 Load Test" ) &&
  echo "Gatling stopped"


  sleep 3
  kill $!

  kubectl delete -f ../deployments/deployment${SAA}.yml &&
  echo "Stoping deployment"${SAA}" deployment"
  wait
  sleep 5

  (cd ../../autoscaler-saa/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 60
  (cd ../../demo1/src/main/kube &&  ./removeApps.sh) &&

  kubectl delete -f ../deployments/deploymentDemo1x${demo1_label}.yml &&

  echo "Demo1 is removed"





