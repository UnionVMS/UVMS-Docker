#!/bin/sh

if [ $# -ne 2 ]; 
    then echo "Requires two arguments [db_name] [postgresql-db-hostname]"
    exit 1
fi

echo "Running module.sql to create tables and init data"
echo "Running asset.sql to create tables and init data"
psql -U asset -d $1 --host=$2 --single-transaction -q -f  eu.europa.ec.fisheries.uvms.asset.liquibase-${unionvms.project.asset.module}.sql >/dev/null
echo "Running  audit.sql to create tables and init data"
psql -U audit -d $1 --host=$2 --single-transaction -q -f  eu.europa.ec.fisheries.uvms.audit.liquibase-${unionvms.project.audit.module}.sql >/dev/null
echo "Running config.sql to create tables and init data"
psql -U config -d $1 --host=$2 --single-transaction -q -f  eu.europa.ec.fisheries.uvms.config.liquibase-${unionvms.project.config.module}.sql >/dev/null
echo "Running exchange.sql to create tables and init data"
psql -U exchange -d $1 --host=$2 --single-transaction -q -f  eu.europa.ec.fisheries.uvms.exchange.liquibase-${unionvms.project.exchange.module}.sql >/dev/null
echo "Running movement.sql to create tables and init data"
psql -U movement -d $1 --host=$2 --single-transaction -q -f  eu.europa.ec.fisheries.uvms.movement.liquibase-${unionvms.project.movement.module}.sql >/dev/null
echo "Running movementrules.sql to create tables and init data"
psql -U movementrules -d $1 --host=$2 -a --single-transaction -q -f  eu.europa.ec.fisheries.uvms.movement-rules.liquibase-${unionvms.project.movementrules.module}.sql >/dev/null
echo "Running incident.sql to create tables and init data"
psql -U incident -d $1 --host=$2 -a --single-transaction -q -f  eu.europa.ec.fisheries.uvms.incident.liquibase-${unionvms.project.incident.module}.sql >/dev/null
echo "Running usm.sql to create tables and init data"
psql -U usm -d $1 --host=$2 --single-transaction -q -f eu.europa.ec.fisheries.uvms.user.liquibase-${unionvms.project.user.module}.sql >/dev/null
echo "Running spatial.sql to create tables and init data"
psql -U spatial -d $1 --host=$2 --single-transaction -q -f eu.europa.ec.fisheries.uvms.spatialSwe.liquibase-${unionvms.project.spatialSwe.module}.sql >/dev/null
echo "Running reporting.sql to create tables and init data"
psql -U reporting -d $1 --host=$2 --single-transaction -q -f eu.europa.ec.fisheries.uvms.reporting.reporting-liquibase-${unionvms.project.reporting.module}.sql >/dev/null
echo "Running activity.sql to create tables and init data"
psql -U activity -d $1 --host=$2 --single-transaction -q -f eu.europa.ec.fisheries.uvms.activity.liquibase-${unionvms.project.activity.module}.sql >/dev/null
echo "Completed module.sql"