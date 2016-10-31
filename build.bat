cd activemq
docker build --tag uvms/activemq .
cd ../postgres
docker build --tag uvms/postgres .
cd ..wildfly
docker build --tag uvms/wildfly .