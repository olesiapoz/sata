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

  ( cd ../../autoscaler/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 30
 
  kubectl apply -f ../deployments/deployment${SAA}.yml &&
  sleep 60

  #collect logs by label app: autoscaler
 

( cd ../../cat_noir/kube &&  kubectl apply  -f deployment.yml -f service.yml -f service-monitor.yml) &&

sleep 30
echo "Demo1 Cat Noir is deployed"

kubectl scale deployment demo1 --replicas=1 &&

(kubectl logs -f -l app=autoscaler > "logs/"${SAA}${simulation}.log) &

for ((n = 0; n < 1; n++)); do
  echo "Gatling fired"
  (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ &&  ./gatling.sh -s ${simulation} -rm local -rd "Cat Noir Load Test" ) &&
  echo "Gatling stopped"
done

sleep 3
  kill $!

  kubectl delete -f ../deployments/deployment${SAA}.yml &&

  sleep 5


  ( cd ../../autoscaler/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  

