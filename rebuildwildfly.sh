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
docker stop wildfly && docker rm -f wildfly &&                                         # Stop wildfly container

### UNCOMMENT IF NEED TOTALLY NEW IMAGES, OTHERWISE THE REFRESH WORKS ALSO (NOT KILLING THE IMAGES)
docker image ls | grep wildfly-release | awk '{print $3}' | xargs docker rmi &&        # Remove wildfly-release image
docker image ls | grep wildfly-unionvms | awk '{print $3}' | xargs docker rmi &&       # Remove wildfly-unionvms image
docker image ls | grep wildfly-base | awk '{print $3}' | xargs docker rmi &&           # Remove wildfly-base image

sleep 2

## Creating images phase :                                    
cd $BASE_DOCKER_DIR		                                                               # Go to Docker project root
pwd
																
printf "\n\nCreating the Docker images from "$BASE_DOCKER_DIR" folder...\n\n" 

printf "\n-->> Building wildfly BASE image...\n\n" &&
cd $BASE_DOCKER_DIR/wildfly-base && mvn clean install -DskipTests -DkipITs=true &&     # Build wildfly-base image

printf "\n-->> Building wildfly UNIONVMS image...\n\n" &&
cd $BASE_DOCKER_DIR/wildfly-unionvms && mvn clean install -DskipTests -DkipITs=true && # Build wildfly-unionvms image

printf "\n-->> Building wildfly FLUXFMC...\n\n" &&
cd $BASE_DOCKER_DIR/wildfly-fluxfmc && mvn clean install -DskipTests -DkipITs=true &&  # Build wildfly-fluxfmc image

## Runinng wildfly container phase : 
printf "\n\nGoing to run wildfly container..\n\n"
# -p 9010:9010 for jconsole
docker image ls | grep wildfly-release | awk '{print $3}' | xargs docker run -it -p 9990:9990 -p 8787:8787 -p 8080:8080 --name wildfly --net-alias wildfly --net=uvms -m 8G -d # Run the wildfly-release image (create container)
printf "\nContainer was started.. Going in the wildfly container now.. See YA..\n\n" 

docker exec -it wildfly bash

printf "source /opt/jboss/aliases.sh"

#docker logs -f wildfly
#shopt -s expand_aliases &&
#sleep 5 && 
#tail -fn 1000 /opt/jboss/wildfly/standalone/log/server.log
#echo "echo alias serverlogs=echo serverlogs" | docker exec --interactive -it wildfly bash -