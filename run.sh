#!/bin/sh
docker rm wildfly activemq postgres
docker network rm uvms
docker network create uvms
docker run -it -p 61616:61616 -p 8161:8161 --name activemq --net-alias activemq -v ~/uvms/activemq:/opt/jboss/activemq/data --net=uvms -d uvms/activemq:5.13.2
docker run -it -p 5433:5432 --name postgres --net-alias postgres --net=uvms -d uvms/postgres-full
sleep 180
docker run -it -p 9990:9990 -p 8787:8787 -p 8080:8080 --name wildfly --net-alias wildfly --net=uvms -v ~/uvms/app/logs:/app/logs -v ~/uvms/wildfly:/opt/jboss/wildfly/standalone/log -m 6G -d uvms/wildfly-full