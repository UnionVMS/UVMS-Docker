docker rm wildfly activemq postgres
docker network rm uvms
docker network create uvms
docker run -it -p 61616:61616 -p 8161:8161 --name activemq --net-alias activemq --net=uvms -v c:/uvms/activemq:/opt/jboss/activemq/data -d uvms/activemq:5.13.2
docker run -it -p 5433:5432 --name postgres --net-alias postgres --net=uvms -d uvms/postgres-full
timeout /t 180 /nobreak
docker run -it -p 9990:9990 -p 8787:8787 -p 8080:8080 --name wildfly --net-alias wildfly --net=uvms -m 6G -d -v c:/uvms/app/logs:/app/logs -v c:/uvms/wildfly:/opt/jboss/wildfly/standalone/log uvms/wildfly-full
