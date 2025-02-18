#! /bin/bash

( cd kube &&  kubectl apply -f deployment.yml -f service.yml -f service-monitor.yml)
wait
echo "Demo1_cat_noir is deployed"
