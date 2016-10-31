## The simple way

On Windows:

* Download Docker from https://download.docker.com/win/stable/InstallDocker.msi
* Install Docker and allow access to Hyper-V
* In the task bar, find the Docker icon and right click it
* Select Open Kitematic and follow the instructions

First time

* Run build.bat
* Run run.bat
* When done, run stop.bat

Second time

* Run start.bat
* When done, run stop.bat

## Build
To rebuild from scratch, add --no-cache between "build" and "--tag" in the commands below

### Wildfly
1. cd wildfly
2. docker build --tag uvms/wildfly .

### Active MQ
1. cd amq
2. docker build --tag uvms/amq .

### PostgreSQL
1. cd postgres
2. docker build --tag uvms/postgres .

## Run
-d detaches container on start, remove to run in terminal

### First run
#### Create network
docker network create uvms

#### Active MQ
docker run -it -p 8161:8161 --name activemq --net-alias activemq --net=uvms -d uvms/amq

#### PostgreSQL
docker run -it -p 5433:5432 --name postgres --net-alias postgres --net=uvms --rm uvms/postgres

#### Wildfly
docker run -it -p 9990:9990 -p 8787:8787 -p 8080:8080 --name wildfly --net-alias wildfly --net=uvms -m 6G -d uvms/wildfly

### Stop

#### Active MQ
docker stop activemq

#### PostgreSQL
docker stop postgres

#### Wildfly
docker stop wildfly

### Second run

#### Active MQ
docker start activemq

#### PostgreSQL
docker start postgres

#### Wildfly
docker start wildfly
