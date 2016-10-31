These containers downloads and setups a complete Union-VMS system, using the latest available binaries, including snapshots, from the nexus at nexus.focus.fish. The binaries are downloaded at build time, meaning that if newer binaries are made available, a rebuild will be required.

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

## The involved way

On Windows:

* Download Docker from https://download.docker.com/win/stable/InstallDocker.msi
* Install Docker and allow access to Hyper-V
* In the task bar, find the Docker icon and right click it
* Select Open Kitematic and follow the instructions

First time:
1. cd amq
2. docker build --tag uvms/amq .
3. cd ..\postgres
4. docker build --tag uvms/postgres .
5. cd ..\wildfly
6. docker build --tag uvms/wildfly .
7. docker network create uvms
8. docker run -it -p 8161:8161 --name activemq --net-alias activemq --net=uvms -d uvms/amq
9. docker run -it -p 5433:5432 --name postgres --net-alias postgres --net=uvms -d uvms/postgres
10. docker run -it -p 9990:9990 -p 8787:8787 -p 8080:8080 --name wildfly --net-alias wildfly --net=uvms -m 6G -d uvms/wildfly

When done:
1. docker stop wildfly
2. docker stop activemq
3. docker stop postgres

Second time:
1. docker start activemq
2. docker start postgres
3. docker start wildfly

To rebuild from scratch, add --no-cache between "build" and "--tag" in the commands below