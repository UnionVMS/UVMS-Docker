## Build

### Wildfly
cd wildfly
docker build --tag uvms/wildfly .

### Active MQ
cd amq
docker build --tag uvms/amq .

### PostgreSQL
cd postgres
docker build --tag uvms/postgres .

## Run

### Create network
docker network create uvms

### Wildfly
docker run -it -p 9990:9990 -p 8787:8787 -p 8080:8080 --name wildfly --net-alias wildfly --net=uvms -m 6G --rm uvms/wildfly

### Active MQ
docker run -it -p 8161:8161 --name activemq --net-alias activemq --net=uvms --rm uvms/amq