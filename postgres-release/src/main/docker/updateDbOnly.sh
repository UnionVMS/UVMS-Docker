#!bin/sh

echo "Running module.sql to create tables and init data"
echo "Running asset.sql to create tables and init data"
psql -U asset -d db71u --single-transaction -q -f  eu.europa.ec.fisheries.uvms.asset.liquibase-${unionvms.project.asset.module}.sql >/dev/null
echo "Running audit.sql to create tables and init data"
psql -U audit -d db71u --single-transaction -q -f  eu.europa.ec.fisheries.uvms.audit.liquibase-${unionvms.project.audit.module}.sql >/dev/null
echo "Running config.sql to create tables and init data"
psql -U config -d db71u --single-transaction -q -f  eu.europa.ec.fisheries.uvms.config.liquibase-${unionvms.project.config.module}.sql >/dev/null
echo "Running exchange.sql to create tables and init data"
psql -U exchange -d db71u --single-transaction -q -f  eu.europa.ec.fisheries.uvms.exchange.liquibase-${unionvms.project.exchange.module}.sql >/dev/null
echo "Running mobterm.sql to create tables and init data"
psql -U mobterm -d db71u --single-transaction -q -f  eu.europa.ec.fisheries.uvms.mobileterminal.liquibase-${unionvms.project.mobileterminal.module}.sql >/dev/null
echo "Running movement.sql to create tables and init data"
psql -U movement -d db71u --single-transaction -q -f  eu.europa.ec.fisheries.uvms.movement.liquibase-${unionvms.project.movement.module}.sql >/dev/null
echo "Running rules.sql to create tables and init data"
psql -U rules -d db71u -a --single-transaction -q -f  eu.europa.ec.fisheries.uvms.rules.liquibase-${unionvms.project.rules.module}.sql >/dev/null
echo "Running usm.sql to create tables and init data"
psql -U usm -d db71u --single-transaction -q -f eu.europa.ec.fisheries.uvms.user.liquibase-${unionvms.project.user.module}.sql >/dev/null
echo "Running spatial.sql to create tables and init data"
psql -U spatial -d db71u --single-transaction -q -f eu.europa.ec.fisheries.uvms.spatial.liquibase-${unionvms.project.spatial.module}.sql >/dev/null
echo "Running mdr.sql to create tables and init data"
psql -U mdr -d db71u --single-transaction -q -f eu.europa.ec.fisheries.uvms.mdr.liquibase-${unionvms.project.mdr.module}.sql >/dev/null
echo "Running activity.sql to create tables and init data"
psql -U activity -d db71u --single-transaction -q -f eu.europa.ec.fisheries.uvms.activity.liquibase-${unionvms.project.activity.module}.sql >/dev/null
echo "Running reporting.sql to create tables and init data"
psql -U reporting -d db71u --single-transaction -q -f eu.europa.ec.fisheries.uvms.reporting.liquibase-${unionvms.project.reporting.module}.sql >/dev/null
echo "Completed module.sql"