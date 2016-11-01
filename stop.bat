if not exist "C:\app\logs" mkdir C:\app\logs
docker stop wildfly
docker logs wildfly > c:\app\logs\wildfly.log
docker cp wildfly:/app/logs c:\app\logs\modules
docker stop activemq
docker logs activemq > c:\app\logs\activemq.log
docker stop postgres
docker logs postgres > c:\app\logs\postgres.log