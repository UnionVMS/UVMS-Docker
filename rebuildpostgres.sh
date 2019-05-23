#!/bin/bash

BASE_DOCKER_DIR=$1

if [ -z ${BASE_DOCKER_DIR} ];then
  BASE_DOCKER_DIR=$(pwd);
  printf "\nDocker project location not passed as variable! Assuming project is located at : $BASE_DOCKER_DIR\n\n" 
 else
  printf "\nDocker project is in : $BASE_DOCKER_DIR\n\n";
fi

# Stoping / cleaning phase :
printf "\nStoping container and cleaning images...\n" 
docker stop postgres && docker rm -f postgres &&                              # Stop postgres container

docker image ls | grep postgres | awk '{print $3}' | xargs docker rmi &&        # Remove postgres image

sleep 2

## Creating images phase :                                    
cd $BASE_DOCKER_DIR		                                                      # Go to Docker project root
pwd
																
printf "\n\nCreating the Docker images from "$BASE_DOCKER_DIR" folder...\n\n" &&

printf "\n-->> Building postgres-base image...\n\n" &&
cd $BASE_DOCKER_DIR/postgres-base && mvn clean install -U -DskipTests -DkipITs=true &&     # Build postgres image
printf "\n-->> Finished building postgres-base image...\n\n" &&

printf "\n-->> Building postgres-release image...\n\n" &&
cd $BASE_DOCKER_DIR/postgres-release && mvn clean install -U -DskipTests -DkipITs=true &&     # Build postgres image
printf "\n-->> Finished building postgres-release image...\n\n" &&

## Runinng postgres container phase :
printf "\n\nGoing to run postgres container..\n\n"

docker image ls | grep postgres-release | awk '{print $3}' | xargs docker run -it -p 5432:5432 --name postgres --net-alias postgres --net=uvms -d   # Run the postgres image (create container)
printf "\nContainer was started correctly.. See YA..\n\n"