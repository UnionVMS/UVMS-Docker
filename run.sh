#!/bin/sh
docker rm wildfly activemq postgres
docker network rm uvms
docker network create uvms
docker run -it -p 8161:8161 --name activemq --net-alias activemq --net=uvms -d uvms/activemq:5.13.2
docker run -it -p 5433:5432 --name postgres --net-alias postgres --net=uvms -d uvms/postgres-full
sleep 60
docker run -it -p 9990:9990 -p 8787:8787 -p 8080:8080 --name wildfly --net-alias wildfly --net=uvms -m 6G -d uvms/wildfly-full