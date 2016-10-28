#### Build

## Wildfly
cd wildfly
docker build --tag uvms/wildfly .

## Active MQ
cd amq
docker build --tag uvms/amq .

#### Run

## Create network
docker network create uvms

## Wildfly
docker run -it -p 9990:9990 -p 8787:8787 --name wildfly --net-alias wildfly --net=uvms --rm uvms/wildfly

## Active MQ
docker run -it -p 8161:8161 --name activemq --net-alias activemq --net=uvms --rm uvms/amq