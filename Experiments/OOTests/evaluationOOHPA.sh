#!/bin/bash

#OO tests SAAx10x60x27x25RPS 'avgRTx4x90x10x90', '4x90x10x90' '12k-8k'

echo "OO tests SAAx10x60x27x25RPS avgRTx4x90x10x90, 4x90x10x90 12k-8k"

simulation='SwitchOnOffSimulationOpo'
#simulation='SwitchOnOffSimulationTesting'
echo "Running "${simulation}" simulation"
hpa='hpaTest27'

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


## SAA test
echo "deleting HPA"
( kubectl delete -f hpa.yml) &&

sleep 30

( cd ../../autoscaler/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
sleep 30
 

  #scale down demo1 to 1
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 90

  echo "instaling "${SAA_label}
  kubectl apply -f deployment${SAA_label}.yml &&
  sleep 60

  kubectl apply -f ${hpa}.yml &&
  sleep 60

  #collect logs by label app: autoscaler
  (kubectl logs -f -l app=autoscaler > "logs/"${hpa}.log) &
  echo "Gatling fired"
  #(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./iterator.sh) &&
  (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./gatling.sh -s  ${simulation} -rm local -rd "dyn50" ) &&
  # (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./iteratorWC.sh ) &&
  echo "Gatling stopped"

  #delete autoscaler
  sleep 3
  kill $!
  (kubectl logs  -l app=autoscaler > "logs/"${hpa}.log) &&
  wait
  sleep 15
  kubectl delete -f deployment${SAA_label}.yml &&
  wait
  sleep 5


  ( cd ../../autoscaler/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 60

echo "Demo1 is removing"
( cd ../../demo1/src/main/kube &&  kubectl delete -f service.yml -f service-monitor.yml)
wait

kubectl delete -f deploymentDemo1x${demo1_label}.yml &&
kubectl delete -f ${hpa}.yml &&
echo "Demo1 is removed"

cp nohup.out logs/nohup.out