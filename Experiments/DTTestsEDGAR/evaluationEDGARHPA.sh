#!/bin/bash

#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 10*90 (3*ThresholdUpdate)
#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
#scenarios ThresholdUpdate period 8*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
date

demo1_label='12k-8k'
hpa='hpaTest34'
SAA='HPATracking'
simulation='ComputerDatabaseSimulationEDGAROpo'
echo "Running "${simulation}" simulation"

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


## SAA test


sleep 30

  ( cd ../../autoscaler/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 30
 
  #scale down demo1 to 1
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 90

  kubectl apply -f deployment${SAA}.yml &&
  kubectl apply -f ${hpa}.yml &&
  sleep 60
  #collect logs by label app: autoscaler
  (kubectl logs -f -l app=autoscaler > logs/${hpa}.log) &

  echo "Gatling fired"
  (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./iteratorEDGAR.sh ) &&
  echo "Gatling stopped"

  #delete autoscaler
  sleep 3
  kill $!

  kubectl delete -f deployment${SAA}.yml &&
  kubectl delete -f ${hpa}.yml &&
  wait
  sleep 5


  ( cd ../../autoscaler/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 60

echo "Demo1 is removing"
( cd ../../demo1/src/main/kube &&  kubectl delete -f service.yml -f service-monitor.yml)
wait

kubectl delete -f deploymentDemo1x${demo1_label}.yml &&
echo "Demo1 is removed"

cp nohup.out logs/nohup.out
