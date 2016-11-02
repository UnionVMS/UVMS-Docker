#!/bin/sh
docker build --tag uvms/postgres-full --no-cache postgres
docker build --tag uvms/wildfly-full --no-cache wildfly