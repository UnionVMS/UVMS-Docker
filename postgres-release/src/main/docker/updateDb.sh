#!bin/sh
JAVA_HOME=/usr/lib/jvm/default-java
export JAVA_HOME

# Prepared transactions
sed -i -e"s/^#max_prepared_transactions = 0.*$/max_prepared_transactions = 200/" /var/lib/postgresql/data/postgresql.conf

# Performance Tuning
sed -i -e"s/^max_connections = 100.*$/max_connections = 200/" /var/lib/postgresql/data/postgresql.conf
sed -i -e"s/^shared_buffers =.*$/shared_buffers = 512MB/" /var/lib/postgresql/data/postgresql.conf
sed -i -e"s/^#effective_cache_size = 128MB.*$/effective_cache_size = 512MB/" /var/lib/postgresql/data/postgresql.conf
sed -i -e"s/^#work_mem = 4MB.*$/work_mem = 16MB/" /var/lib/postgresql/data/postgresql.conf

/etc/init.d/postgresql restart

cd /liquibase/usm/database/liquibase
mvn liquibase:update -Ddb.url=jdbc:postgresql://localhost:5432/db71u -Ddb.user=usm -Ddb.passwd=usm

cd /liquibase/asset/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/audit/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/config/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/exchange/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/movement/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/mobterm/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/rules/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/spatial/LIQUIBASE

mvn liquibase:update -Ppostgres,exec -Ddb.url=jdbc:postgresql://localhost:5432/db71u

cd /liquibase/reporting/LIQUIBASE
mvn liquibase:update -Ppostgres,exec -Ddb.url=jdbc:postgresql://localhost:5432/db71u

cd /liquibase/activity/LIQUIBASE
mvn liquibase:update -Ppostgres,exec,testdata -Ddb.url=jdbc:postgresql://localhost:5432/db71u

cd /liquibase/mdr/LIQUIBASE
mvn liquibase:update -Ppostgres,exec,testdata -Ddb.url=jdbc:postgresql://localhost:5432/db71u

rm -rf /home/postgres/.m2/repository

psql -U spatial -d db71u -c "update system_configurations set value = 'http://localhost:28080/geoserver/' where name='geo_server_url'"

echo "All uvms databases created"