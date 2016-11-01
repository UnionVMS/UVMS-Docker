docker stop wildfly
docker logs wildfly > /app/logs/wildfly.log
docker cp wildfly:/app/logs c:\app/logs/modules
docker stop activemq
docker logs activemq > /app/logs/activemq.log
docker stop postgres
docker logs postgres > /app/logs/postgres.log