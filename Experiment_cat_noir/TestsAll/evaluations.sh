#!/bin/bash

#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 10*90 (3*ThresholdUpdate)
#scenarios ThresholdUpdate period 4*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
#scenarios ThresholdUpdate period 8*90, PeriodsForEvaluation 20*90 (6*ThresholdUpdate)
#----------------------Leave uncommented--------------------------------------------
date

simulation='ComputerDatabaseSimulation'
#SAA='SAAx9x60x55x27RPSx0.4RT'
SAA='SAAx10x60x55x25RPSx0.4RT_mid75'
DMAR='DMARx55x0.13RT'
echo "Running "${simulation}" simulation"

date
(cd ../../demo1/ &&  ./removeApps.sh) &&
(cd ../../cat_noir/ &&  ./removeApps.sh) &&
sleep 30

#(cd ../../demo1/ && sudo ./apps${demo1_label}.sh) &&
#sleep 75

( cd ../../cat_noir/kube &&  kubectl apply  -f deployment.yml -f service.yml -f service-monitor.yml) &&
echo "Demo1 is deployed"


( cd ../../autoscaler/src/main/kube &&  kubectl delete -f deployment.yml -f service.yml -f autoscalerRBAC.yaml) &&
( cd ../../prod/HPAs &&  kubectl delete -f hpa.yml) &&
( cd ../../autoscaler-dmar/src/main/kube &&  kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
sleep 45

#declare -a arrOfExperiments=('4x90x10x90'  )
#declare -a arrOfExperiments=('SMAx6x90x20x90x0.4RT' )
declare -a arrOfExperiments=('SMAx4x90x10x90x0.4RT' )
arr_size=${#arrOfExperiments[@]}
echo $arr_size

#declare -a arrOfSimulations=('iteratorWC_cat' 'iteratorEDGAR_cat')
declare -a arrOfSimulations=( 'iteratorEDGAR_cat')
sim_arr_size=${#arrOfSimulations[@]}
echo $sim_arr_size
#----------------------Leave uncommented till here--------------------------------------------

for ((i = 0; i < (arr_size); i++)); do
  for ((n = 0; n < (sim_arr_size); n++)); do
  #deploy new autoscaler 4x90x10x90
  date
  (kubectl apply -f ../deployments/hpa.yml) &&
  sleep 10

  (cd ../../autoscaler/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&

  
  echo "Running deployment "${arrOfExperiments[i]}" deployment for "${arrOfSimulations[n]} "Simulation"
  sleep 10

  #scale down demo1 to 1
  kubectl scale deployment demo1 --replicas=1 &&
  sleep 30

  kubectl apply -f ../deployments/deployment${arrOfExperiments[i]}.yml &&
  sleep 60
  #collect logs by label app: autoscaler
  (kubectl logs -f -l app=autoscaler > "logs/"${arrOfExperiments[i]}${arrOfSimulations[n]}.log) &
  #kubectl logs -f -l app=autoscaler > logs/SMAx6x90x20x90x0.4RTiteratorWC_cat.log
  #run gatling
  echo "Gatling fired"
  #(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./iterator.sh) &&
  #(cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ && ./gatling.sh -s  ${simulation} -rm local -rd "dyn50" ) &&
  (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ &&  "./${arrOfSimulations[n]}.sh" ) &&
  echo "Gatling stopped"

  # #delete autoscaler
  sleep 3
  kill $!

  kubectl delete -f ../deployments/deployment${arrOfExperiments[i]}.yml &&
  sleep 5
  echo "Deleted deployment"${arrOfExperiments[i]}" deployment"

  ( cd ../../autoscaler/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
  sleep 10
   echo "Deleted deployment"${arrOfExperiments[i]}" deployment"

    done
done
#----------------------Leave uncommented--------------------------------------------
echo "deleting HPA"
(kubectl delete -f ../deployments/hpa.yml) &&
sleep 5
#----------------------------------------------------------------------------------

#./evaluationsHPA.sh &&
cp nohup.out logs/nohup.out
echo "done"

## -----------------SAA test------------------------------------------------------------------------
# n=0
# for ((n = 0; n < (sim_arr_size); n++)); do
#   echo "SAA testing"
  

#   ( cd ../../autoscaler-saa/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&
#   sleep 30

  
# ( cd ../../cat_noir/kube &&  kubectl apply  -f deployment.yml -f service.yml -f service-monitor.yml) &&
# echo "Demo1 is deployed"


#   echo "Installing CPA"
#   ( cd ../../custom-pod-autoscaler && ./cpa.sh )&&
#   sleep 30

#    #scale down demo1 to 1
#   kubectl scale deployment demo1 --replicas=1 &&
#   sleep 20

#   kubectl apply -f ../deployments/deployment${SAA}.yml &&
#   sleep 60
#   #collect logs by label app: autoscaler
#   (kubectl logs -f -l app=autoscaler > "logs/"${SAA}${arrOfSimulations[n]}.log) &
#   #scale down demo1 to 1

#   sleep 60

#    echo "Gatling fired"
#   (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ &&  "./${arrOfSimulations[n]}.sh" ) &&
#   echo "Gatling stopped"


#   sleep 3
#   kill $!

#   kubectl delete -f ../deployments/deployment${SAA}.yml &&
#   echo "Running deployment"${SAA}" deployment"
#   wait
#   sleep 5


#   echo "Removing CPA"
#   ( cd ../../custom-pod-autoscaler && ./removeCpa.sh )
#   wait

#   (cd ../../autoscaler-saa/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
#   sleep 60
#   (cd ../../cat_noir/ &&  ./removeApps.sh) &&
#   echo "Demo1 is removed"

# done

# cp nohup.out logs/nohup.out


#------------------------------DMAR---------------------------------------
# n=0
# for ((n = 0; n < (sim_arr_size); n++)); do
#   echo "DMAR testing"

#   ( cd ../../autoscaler-dmar/src/main/kube &&  kubectl apply -f service.yml -f autoscalerRBAC.yaml) &&

#   sleep 30

  
# ( cd ../../cat_noir/kube &&  kubectl apply  -f deployment.yml -f service.yml -f service-monitor.yml) &&
# echo "Demo1 is deployed"


#   echo "Installing CPA"
#   ( cd ../../custom-pod-autoscaler && ./cpa.sh )&&
#   sleep 30

#    #scale down demo1 to 1
#   kubectl scale deployment demo1 --replicas=1 &&
#   sleep 20

#   kubectl apply -f ../deployments/deployment${DMAR}.yml &&
#   sleep 60
#   #collect logs by label app: autoscaler
#   (kubectl logs -f -l app=autoscaler > "logs/"${DMAR}${arrOfSimulations[n]}.log) &
#   #scale down demo1 to 1

#   sleep 60

#    echo "Gatling fired"
#   (cd ../../gatling-charts-highcharts-bundle-3.9.0/bin/ &&  "./${arrOfSimulations[n]}.sh" ) &&
#   echo "Gatling stopped"


#   sleep 3
#   kill $!

#   kubectl delete -f ../deployments/deployment${DMAR}.yml &&
#   echo "Running deployment"${DMAR}" deployment"
#   wait
#   sleep 5


#   echo "Removing CPA"
#   ( cd ../../custom-pod-autoscaler && ./removeCpa.sh )
#   wait

#   (cd ../../autoscaler-dmar/src/main/kube && kubectl delete -f service.yml -f autoscalerRBAC.yaml) &&
#   sleep 60
#   (cd ../../cat_noir/ &&  ./removeApps.sh) &&
#   echo "Demo1 is removed"

# done

# cp nohup.out logs/nohup.out