These containers downloads and setups a complete Union-VMS system, using the latest available binaries, including snapshots, from the nexus at nexus.focus.fish. The binaries are downloaded at build time, meaning that if newer binaries are made available, a rebuild will be required.

# Build & Run 

Remember to always run git pull in this repository before rebuilding!

## Nightlies

[![Build Status](https://travis-ci.org/UnionVMS/UVMS-Docker.svg?branch=master)](https://travis-ci.org/UnionVMS/UVMS-Docker)

Nightly builds are currently not available, however as soon as the guys at Travis-CI activates it, there will be new uvms/wildfly-full and uvms/postgres-full available every night. If you want to check out the build, have a look at https://travis-ci.org/UnionVMS/UVMS-Docker

Builds are also triggered on commit to this repository.

## The simple way

On Windows 10:

* Download Docker from https://download.docker.com/win/stable/InstallDocker.msi
* Install Docker and allow access to Hyper-V
* Share your C-drive with Docker: https://docs.docker.com/docker-for-windows/#shared-drives (For volume mounting, access to log files)
* In the task bar, find the Docker icon and right click it
* Select Open Kitematic and follow the instructions

First time

* Run docker-compose.exe up -d
* When done run docker-compose stop

Second time

* Run docker-compose start
* When done, run docker-compose stop

If you need to rebuild the containers, run docker-compose.exe up -d --build

Please note that some modules will not be deployed on start since they require other modules to start first. To deploy the remaining modules, either:

Run docker exec wildfly /opt/jboss/wildfly/bin/jboss-cli.sh --connect --command="/subsystem=deployment-scanner/scanner=default:write-attribute(name="auto-deploy-zipped",value=true)"

OR

* Go to http://localhost:9990/console/App.html#deployment-scanner
* Select the deployment scanner named "default"
* Click "Edit"
* Mark the "Enabled" checkbox
* Click "Save"

### Accessing stuff

| Thing      | Host      | Port | Username      | Password | Access with | Access how                        |
| ---------- | --------- | ---- | ------------- | -------- | ----------- | --------------------------------- |
| Wildfly    | localhost | 9990 | admin         | admin    | Browser     | http://localhost:9990             |
| ActiveMQ   | localhost | 8161 | admin         | admin    | Browser     | http://localhost:8161             |
| UVMS       | localhost | 8080 | vms_admin_com | password | Browser     | http://localhost:8080/unionvms    |
| PostgreSQL | localhost | 5433 | postgres      | postgres | PgAdmin     | See https://www.pgadmin.org/docs/ |

## The involved way

On Windows 10:

* Download Docker from https://download.docker.com/win/stable/InstallDocker.msi
* Install Docker and allow access to Hyper-V
* In the task bar, find the Docker icon and right click it
* Select Open Kitematic and follow the instructions

First time:

1. docker build --tag uvms/amq amq/Dockerfile
2. docker build --tag uvms/postgres postgres/Dockerfile
3. docker build --tag uvms/wildfly wildfly/Dockerfile
4. docker network create uvms
5. docker run -it -p 8161:8161 --name activemq --net-alias activemq --net=uvms -d uvms/amq
6. docker run -it -p 5433:5432 --name postgres --net-alias postgres --net=uvms -d uvms/postgres
7. docker run -it -p 9990:9990 -p 8787:8787 -p 8080:8080 --name wildfly --net-alias wildfly --net=uvms -m 6G -d uvms/wildfly

When done:

1. docker stop wildfly
2. docker stop activemq
3. docker stop postgres

Second time:

1. docker start activemq
2. docker start postgres
3. docker start wildfly

To rebuild from scratch, add --no-cache between "build" and "--tag" in the commands below

# Some explanations

## docker build
docker build --tag uvms/amq amq/Dockerfile

This builds a container with tag "uvms/amq" using the Dockerfile located at amq/Dockerfile.

## docker network
docker network create uvms

This creates a network named "uvms" used by the three containers to communicate between each other.

## docker run
docker run -it -p 8161:8161 --name activemq --net-alias activemq --net=uvms -d uvms/amq

This starts a detached container named "activemq" with the same net alias, using the "uvms" network. The container is based on "uvms/amq". Docker port 8161 is mapped to host port 8161. STDIN is kept open and a pseudo-tty is allocated.

* -it <- Keep STDIN open and allocate pseudo-tty
* -p 8161:8161 <- Map container port to host port, format is host:container
* --name activemq <- Name the container, for easier handling
* --net-alias activemq <- Set the net-alias, used for networking
* --net=uvms <- Specify network to use
* -d <- Run in detached mode, container runs in background
* uvms/amq <- Container tag
