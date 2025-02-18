#! /bin/bash
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color


helm delete grafana
wait

kubectl delete -f templates/configMap.yaml

echo -e "${GREEN}Grafana is deleted${NC}"

