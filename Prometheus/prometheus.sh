#! /bin/bash
echo "Creating persitant volumes"

(cd ../prod && ./volumes.sh)

wait
set -e

helm upgrade --install prometheus -f values.yaml prometheus-community/prometheus --version=15.16.0

wait 


echo "patching helming"
kubectl patch ds prometheus-node-exporter --type "json" -p '[{"op": "remove", "path" : "/spec/template/spec/containers/0/volumeMounts/2/mountPropagation"}]'
wait

echo "finished"
