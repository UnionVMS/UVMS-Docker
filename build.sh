#!/bin/sh
docker pull uvms/postgres:9.3
docker pull uvms/wildfly:8.2.0
docker build --tag uvms/postgres-full --no-cache postgres
docker build --tag uvms/wildfly-full --no-cache wildfly