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
docker stop activemq && docker rm -f activemq &&                              # Stop activemq container

#docker image ls | grep activemq | awk '{print $3}' | xargs docker rmi &&        # Remove activemq image

sleep 2

## Creating images phase :                                    
cd $BASE_DOCKER_DIR		                                                      # Go to Docker project root
pwd
																
printf "\n\nCreating the Docker images from "$BASE_DOCKER_DIR" folder...\n\n" 

printf "\n-->> Building activemq image...\n\n" &&
cd $BASE_DOCKER_DIR/amq && mvn clean install -DskipTests -DkipITs=true &&     # Build activemq image

## Runinng wildfly container phase : 
printf "\n\nGoing to run activemq container..\n\n"

docker image ls | grep activemq | awk '{print $3}' | xargs docker run -it -p 8161:8161 -p 61616:61616 --name activemq --net-alias activemq --net=uvms -m 2G -d  # Run the activemq image (create container)
printf "\nContainer was started correctly.. See YA..\n\n"