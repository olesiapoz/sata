#!/bin/bash

rm ../user-files/resources/batch.csv

#file='../user-files/resources/wc_d78_1737_2137.csv'
file='../user-files/resources/EDGAR_cut.csv'
simulation='ComputerDatabaseSimulationEDGAROpoCat'

# Get the number of lines in the file
n_lines=$(wc -l < ${file})

# Calculate the number of iterations
n_iterations=3
n=$(($n_lines/$n_iterations + 1 ))

# Create a new files
split --numeric-suffixes=1 -l $n  ${file} ../user-files/resources/batch_  #; rename 's/_0{1,}([0-9]+)/_$1/' ../user-files/resources/batch_*
sleep 80
./gatling.sh -s ${simulation} -rm local -rd "EDGAR_cat_start_50_12k" #&

declare -a arr=()

arr_size=$(find ../user-files/resources/ -maxdepth 1 -name "batch_*"  | wc -l)
echo $arr_size

for ((i=1;i<=(arr_size);i++)); do
  if [ $i  -lt 10 ]
    then
      arr+=("../user-files/resources/batch_0"$i)
  else
  arr+=("../user-files/resources/batch_"$i)
  fi
  done

#declare -a arr=("../user-files/resources/batch_01" "../user-files/resources/batch_02")

# Loop through the iterations
for (( i=1; i<${#arr[@]}; i++ )); do
  rm ../user-files/resources/batch.csv
  # Copy the range of lines to the new file
  echo ${arr[i]}
  cp ${arr[i]} ../user-files/resources/batch.csv

#start gatling
  ./gatling.sh -s ${simulation} -rm local -rd "EDGAR_start_50_12k" #&
done

for (( i=1; i<=${#arr[@]}; i++ )); do
  rm ../user-files/resources/batch.csv
  # Copy the range of lines to the new file
  rm ${arr[i]}
done

rm ../user-files/resources/batch_01