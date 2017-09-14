#!bin/sh
JAVA_HOME=/usr/lib/jvm/default-java
export JAVA_HOME

# Prepared transactions
sed -i -e"s/^#max_prepared_transactions = 0.*$/max_prepared_transactions = 100/" /var/lib/postgresql/data/postgresql.conf

# Performance Tuning
#sed -i -e"s/^max_connections = 100.*$/max_connections = 1000/" /var/lib/postgresql/data/postgresql.conf
#sed -i -e"s/^shared_buffers =.*$/shared_buffers = 16GB/" /var/lib/postgresql/data/postgresql.conf
#sed -i -e"s/^#effective_cache_size = 128MB.*$/effective_cache_size = 48GB/" /var/lib/postgresql/data/postgresql.conf
#sed -i -e"s/^#work_mem = 1MB.*$/work_mem = 16MB/" /var/lib/postgresql/data/postgresql.conf
#sed -i -e"s/^#maintenance_work_mem = 16MB.*$/maintenance_work_mem = 2GB/" /var/lib/postgresql/data/postgresql.conf
#sed -i -e"s/^#checkpoint_segments = .*$/checkpoint_segments = 32/" /var/lib/postgresql/data/postgresql.conf
#sed -i -e"s/^#checkpoint_completion_target = 0.5.*$/checkpoint_completion_target = 0.7/" /var/lib/postgresql/data/postgresql.conf
#sed -i -e"s/^#wal_buffers =.*$/wal_buffers = 16MB/" /var/lib/postgresql/data/postgresql.conf
#sed -i -e"s/^#default_statistics_target = 100.*$/default_statistics_target = 100/" /var/lib/postgresql/data/postgresql.conf

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