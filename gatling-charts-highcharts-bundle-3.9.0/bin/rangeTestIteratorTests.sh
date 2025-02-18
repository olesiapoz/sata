#!/bin/bash

date
echo ${simulation}
rm ../user-files/resources/batch.csv

file='../user-files/resources/wc_d78_1937_2137.csv'
#simulation='ComputerDatabaseSimulation'
simulation='ComputerDatabaseSimulationOpoTest'

# Get the number of lines in the file
n_lines=$(wc -l < ${file})

# Calculate the number of iterations
n_iterations=1
n=$(($n_lines/$n_iterations + 1 ))

# Create a new files
split --numeric-suffixes=0 -l $n  ${file} ../user-files/resources/batch_  #; rename 's/_0{1,}([0-9]+)/_$1/' ../user-files/resources/batch_*

#./gatling.sh -s ${simulation} -rm local -rd "dyn50" #&

declare -a arr=()

arr_size=$(find ../user-files/resources/ -maxdepth 1 -name "batch_*"  | wc -l)
echo $arr_size

for ((i=0;i<(arr_size);i++)); do
  if [ $i  -lt 10 ]
    then
      arr+=("../user-files/resources/batch_0"$i)
  else
  arr+=("../user-files/resources/batch_"$i)
  fi
done

#declare -a arr=("../user-files/resources/batch_01" "../user-files/resources/batch_02")

# Loop through the iterations
for (( i=0; i<${#arr[@]}; i++ )); do
  rm ../user-files/resources/batch.csv
  # Copy the range of lines to the new file
  echo ${arr[i]}
  cp ${arr[i]} ../user-files/resources/batch.csv

#start gatling
  ./gatling.sh -s ${simulation} -rm local -rd "dyn50" #&
done

for (( i=0; i<${#arr[@]}; i++ )); do
  rm ../user-files/resources/batch.csv
  # Copy the range of lines to the new file
  rm ${arr[i]}
done