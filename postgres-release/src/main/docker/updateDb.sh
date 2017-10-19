#!bin/sh

# Prepared transactions
sed -i -e"s/^#max_prepared_transactions = 0.*$/max_prepared_transactions = 200/" /var/lib/postgresql/data/postgresql.conf

# Performance Tuning
sed -i -e"s/^max_connections = 100.*$/max_connections = 200/" /var/lib/postgresql/data/postgresql.conf
sed -i -e"s/^shared_buffers =.*$/shared_buffers = 512MB/" /var/lib/postgresql/data/postgresql.conf
sed -i -e"s/^#effective_cache_size = 128MB.*$/effective_cache_size = 512MB/" /var/lib/postgresql/data/postgresql.conf
sed -i -e"s/^#work_mem = 4MB.*$/work_mem = 16MB/" /var/lib/postgresql/data/postgresql.conf

# Reload configuration
psql -U postgres postgres -c "SELECT pg_reload_conf()"


echo "Running module.sql to create tables and init data"
psql -U asset -d db71u -a -f  /var/lib/postgresql/eu.europa.ec.fisheries.uvms.asset.liquibase-${unionvms.project.asset.module}.sql
psql -U audit -d db71u -a -f  /var/lib/postgresql/eu.europa.ec.fisheries.uvms.audit.liquibase-${unionvms.project.audit.module}.sql
psql -U config -d db71u -a -f  /var/lib/postgresql/eu.europa.ec.fisheries.uvms.config.liquibase-${unionvms.project.config.module}.sql
psql -U exchange -d db71u -a -f  /var/lib/postgresql/eu.europa.ec.fisheries.uvms.exchange.liquibase-${unionvms.project.exchange.module}.sql
psql -U mobterm -d db71u -a -f  /var/lib/postgresql/eu.europa.ec.fisheries.uvms.mobileterminal.liquibase-${unionvms.project.mobileterminal.module}.sql
psql -U movement -d db71u -a -f  /var/lib/postgresql/eu.europa.ec.fisheries.uvms.movement.liquibase-${unionvms.project.movement.module}.sql
psql -U rules -d db71u -a -f  /var/lib/postgresql/eu.europa.ec.fisheries.uvms.rules.liquibase-${unionvms.project.rules.module}.sql

psql -U usm -d db71u -a -f /var/lib/postgresql/eu.europa.ec.fisheries.uvms.user.liquibase-2.0.3-SNAPSHOT.sql
psql -U spatial -d db71u -a -f /var/lib/postgresql/eu.europa.ec.fisheries.uvms.spatial.liquibase-1.0.5-SNAPSHOT.sql
psql -U mdr -d db71u -a -f /var/lib/postgresql/eu.europa.ec.fisheries.uvms.mdr.liquibase-1.0.2-SNAPSHOT.sql
psql -U activity -d db71u -a -f /var/lib/postgresql/eu.europa.ec.fisheries.uvms.activity.liquibase-1.0.1-SNAPSHOT.sql

echo "Completed module.sql"


echo "Docker specific update"
psql -U spatial -d db71u -c "update system_configurations set value = 'http://localhost:28080/geoserver/' where name='geo_server_url'"
echo "Docker specific completed"


echo "All uvms databases created"