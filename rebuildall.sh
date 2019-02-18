#!/bin/bash

BASE_DOCKER_DIR=$1

if [ -z ${BASE_DOCKER_DIR} ];then
  BASE_DOCKER_DIR=$(pwd);
  printf "\nDocker project location not passed as variable! Assuming project is located at : $BASE_DOCKER_DIR\n\n" 
 else
  printf "\nDocker project is in : $BASE_DOCKER_DIR\n\n";
fi

# Rebuilding/running each image :

printf "\nRebuilding \n"
/bin/bash rebuildamq.sh
printf "\nFinished Rebuilding \n"

printf "\nRebuilding \n"
/bin/bash rebuildpostgres.sh
printf "\nFinished Rebuilding \n"

printf "\nRebuilding \n"
/bin/bash rebuildwildfly.sh
printf "\nFinished Rebuilding \n"