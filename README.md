These containers downloads and setups a complete Union-VMS system, using the latest available binaries, including snapshots, from the nexus at nexus.focus.fish. The binaries are downloaded at build time, meaning that if newer binaries are made available, a rebuild will be required.

# Build & Run 

Remember to always run git pull in this repository before rebuilding!

## Nightlies

[![Build Status](https://travis-ci.org/UnionVMS/UVMS-Docker.svg?branch=master)](https://travis-ci.org/UnionVMS/UVMS-Docker)

Nightly builds are currently not available, however as soon as the guys at Travis-CI activates it, there will be new uvms/wildfly-full and uvms/postgres-full available every night. If you want to check out the build, have a look at https://travis-ci.org/UnionVMS/UVMS-Docker

Builds are also triggered on commit to this repository.

## The simple way

### On Windows 10:

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

### On Windows 7:

    Note: The docker files needs to run in Linux. Before following the steps, first change the file EOL to unix format for all the files

1. Download Docker from https://www.docker.com/products/docker-toolbox **Use Docker tool box version 1.12.3**
2. Install Docker toolbox. This will fetch all the dependent softwares like git, virtualbox etc. Restart the system after install is complete.
3. Locate kitematic and open the application. By default virtual box is not choosen as the host, so there will an error during start of kitematic. When you see the error start again with choosing start with Virtualbox.
4. In case if it fails again, there will be an option in kitematic to delete VM and recreate the VM.
5. Go to Oracle VM virtual box manager and power off the VM.
6. Increase the memory to 6 GB. (Settings --> System --> Base Memory)
7. Mount a folder which can be shared between VM and windows 7. Goto settings --> shared folder --> new. Choose a folder path and name it "uvms-logs". (When docker starts there will be log folders created for activemq, wildfly and APP inside this)
8. Start the VM. (Headless start)
9. Go to docker cli from kitematics.
10. Verify that uvms-logs is available in the VM by this command.

		- docker-machine ssh default
		- cd /uvms-logs (This should be empty)
		- exit
11. Go to virtual box and add new port forwarding settings. Network --> Adapter1 --> Advanced --> Port Forwarding
		Name : JMX
		Protocol : TCP
		Host IP : Your machine IP (e.g. 10.142....)
		Host Port : 29990
		Guest IP : leave it blank
		Guest Port : 29990
		
12. To run the application :
       * Option 1 (From docker compose file):
         start with docker-compose.yml file. Go to root of the application and execute:
        
        docker-compose.exe up -d
        This will download the released version of the images and create containers out of it.

       * Option 2 (Building locally):
         1. Go to docker-images directory.
         2. Execute : mvn clean install
            `Add -PskipDependency to avoid JBOSS deployment dependency`
            `Add -Dfocus-pom.enforce.jdk.version.disabled=true property if using JDK > 7`
            `Add -Pw7 -Dvmhost={dockermachine IP} -Dvmport={dockermachine tcp port} -Dcertpath={docker machine certificate path} 
            if maven build fails to find the host and certificate automatically.
            Not required for Linux VM`
            
            Note : To fine certificate path, host and port use :docker-machine env default.
            By default vmhostname is 192.168.99.100 and vmport is 2376.
            If it is same for your docker than skip -Dvmhost={dockermachine IP} -Dvmport={dockermachine tcp port}
            
         3. Once the build is over, go to relese-test and execute:
            mvn docker:start -Dhostname={dockermachine IP}
            `Skip -Dhostname={dockermachine IP} for linux VM`
            `Add -Dfocus-pom.enforce.jdk.version.disabled=true property if using JDK > 7`
            
         4. Go to kitematic to see the containers and wait until wildfly boot is finished.   
        
### Accessing stuff

| Thing      | Host      | Port | Username      | Password | Access with | Access how                         |
| ---------- | --------- | ---- | ------------- | -------- | ----------- | ---------------------------------- |
| Wildfly    | localhost | 9990 | admin         | admin    | Browser     | http://localhost:29990             |
| ActiveMQ   | localhost | 8161 | admin         | admin    | Browser     | http://localhost:18161             |
| UVMS       | localhost | 8080 | vms_admin_com | password | Browser     | http://localhost:28080/unionvms    |
| PostgreSQL | localhost | 5433 | postgres      | postgres | PgAdmin     | See https://www.pgadmin.org/docs/  |

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

## Fix for Linux virtial Machine containing the docker

The following instructions should be followed if there are problems installing libcurl3 and curl when issuing 'docker-compose up'

1. Get the address of our DNS server: ```nmcli dev show | grep 'IP4.DNS'```

		The output will be similar to the following:
		IP4.DNS[1]:                             10.128.0.18
		IP4.DNS[2]:                             192.168.16.254

2. sudo touch /etc/docker/daemon.json
3. sudo nano /etc/docker/daemon.json (and use the previous IPs in the dns array)

		{
			"dns": ["10.128.0.18", "192.168.16.254", "8.8.8.8"]
		}

4. sudo service docker restart
5. You can now run ```docker-compose up -d --build```
