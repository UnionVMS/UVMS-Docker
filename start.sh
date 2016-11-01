#!/bin/sh
docker start activemq
docker start postgres
sleep 30
docker start wildfly