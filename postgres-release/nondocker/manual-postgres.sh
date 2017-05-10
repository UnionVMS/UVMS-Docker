#!/bin/bash


## Requires
##
## run as postgresql
## Manual run of postgres-base/setup.sql 
## valid settings.xml in .m2/settings.xml
##


GITHUB=https://github.com/UnionVMS

ASSET_VERSION=3.0.4
CONFIG_VERSION=3.0.4
AUDIT_VERSION=3.0.4
EXCHANGE_VERSION=3.0.4
MOVEMENT_VERSION=3.0.7
MOBILE_TERMINAL_VERSION=3.0.4
RULES_VERSION=3.0.3
SPATIAL_VERSION=0.5.2
REPORTING_VERSION=0.5.2
USM_VERSION=2.0.0


mkdir -p ~/liquibase/config \
    && mkdir -p ~/liquibase/asset \
    && mkdir -p ~/liquibase/audit \
    && mkdir -p ~/liquibase/movement \
    && mkdir -p ~/liquibase/exchange \
    && mkdir -p ~/liquibase/mobterm \
    && mkdir -p ~/liquibase/rules \
    && mkdir -p ~/liquibase/spatial \
    && mkdir -p ~/liquibase/reporting \
    && mkdir -p ~/liquibase/usm


# cp step is important, uvms/postgres runs a script that runs maven in ~/liquibase/MODULE for all but USM.
# USM liquibase should be located in ~/liquibase/usm/database~/liquibase/

# Asset
wget -O ~/liquibase/asset/asset-dbaccess-$ASSET_VERSION.zip $GITHUB/UVMS-AssetModule-DB/archive/asset-dbaccess-$ASSET_VERSION.zip
unzip -q ~/liquibase/asset/asset-dbaccess-$ASSET_VERSION.zip -d ~/liquibase/asset
cp -r ~/liquibase/asset/UVMS-AssetModule-DB-asset-dbaccess-$ASSET_VERSION/* ~/liquibase/asset/

# Config
wget -O $~/liquibase/config/config-dbaccess-$CONFIG_VERSION.zip GITHUB/UVMS-ConfigModule-DB/archive/config-dbaccess-$CONFIG_VERSION.zip
unzip -q ~/liquibase/config/config-dbaccess-$CONFIG_VERSION.zip -d ~/liquibase/config
cp -r ~/liquibase/config/UVMS-ConfigModule-DB-config-dbaccess-$CONFIG_VERSION/* ~/liquibase/config/

# Audit
wget -O ~/liquibase/audit/audit-dbaccess-$AUDIT_VERSION.zip $GITHUB/UVMS-AuditModule-DB/archive/audit-dbaccess-$AUDIT_VERSION.zip
unzip -q ~/liquibase/audit/audit-dbaccess-$AUDIT_VERSION.zip -d ~/liquibase/audit
cp -r ~/liquibase/audit/UVMS-AuditModule-DB-audit-dbaccess-$AUDIT_VERSION/* ~/liquibase/audit/

# Exchange
wget -O ~/liquibase/exchange/exchange-dbaccess-$EXCHANGE_VERSION.zip $GITHUB/UVMS-ExchangeModule-DB/archive/exchange-dbaccess-$EXCHANGE_VERSION.zip
unzip -q ~/liquibase/exchange/exchange-dbaccess-$EXCHANGE_VERSION.zip -d ~/liquibase/exchange
cp -r ~/liquibase/exchange/UVMS-ExchangeModule-DB-exchange-dbaccess-$EXCHANGE_VERSION/* ~/liquibase/exchange/

# Movement  https://github.com/UnionVMS/
wget -O ~/liquibase/movement/movement-$MOVEMENT_VERSION.zip $GITHUB/UVMS-MovementModule-APP/archive/movement-$MOVEMENT_VERSION.zip
unzip -q ~/liquibase/movement/movement-$MOVEMENT_VERSION.zip -d ~/liquibase/movement
cp -r ~/liquibase/movement/UVMS-MovementModule-APP-movement-$MOVEMENT_VERSION/* ~/liquibase/movement/

# Mobile Terminal
wget -O  ~/liquibase/mobterm/mobileterminal-dbaccess-$MOBILE_TERMINAL_VERSION.zip $GITHUB/UVMS-MobileTerminalModule-DB/archive/mobileterminal-dbaccess-$MOBILE_TERMINAL_VERSION.zip
unzip -q ~/liquibase/mobterm/mobileterminal-dbaccess-$MOBILE_TERMINAL_VERSION.zip -d ~/liquibase/mobterm
cp -r ~/liquibase/mobterm/UVMS-MobileTerminalModule-DB-mobileterminal-dbaccess-$MOBILE_TERMINAL_VERSION/* ~/liquibase/mobterm/

# Rules
wget -O ~/liquibase/rules/rules-dbaccess-$RULES_VERSION.zip $GITHUB/UVMS-RulesModule-DB/archive/rules-dbaccess-$RULES_VERSION.zip
unzip -q ~/liquibase/rules/rules-dbaccess-$RULES_VERSION.zip -d ~/liquibase/rules
cp -r ~/liquibase/rules/UVMS-RulesModule-DB-rules-dbaccess-$RULES_VERSION/* ~/liquibase/rules/

# Spatial - Remeber to update this when spatial starts releasing on Github
##wget -O $GITHUB/UVMS-SpatialModule-DB/archive/UVMS-SpatialModule-DB-$SPATIAL_VERSION.zip ~/liquibase/spatial
##unzip -q ~/liquibase/spatial/spatial-dbaccess-$SPATIAL_VERSION -d ~/liquibase/spatial
##cp -r ~/liquibase/spatial/UVMS-SpatialModule-DB-spatial-dbaccess-$SPATIAL_VERSION/* ~/liquibase/spatial/
#No release tags for spatial, commit using 0.5.2 commit
#wget -O $GITHUB/UVMS-SpatialModule-DB/archive/418c616e9daf5c1274097fb32216b18b7f3a019e.zip ~/liquibase/spatial
#unzip -q ~/liquibase/spatial/418c616e9daf5c1274097fb32216b18b7f3a019e.zip -d ~/liquibase/spatial
#cp -r ~/liquibase/spatial/UVMS-SpatialModule-DB-418c616e9daf5c1274097fb32216b18b7f3a019e/* ~/liquibase/spatial/
# Latest commit Dec 9 2016 
wget -O ~/liquibase/spatial/f296d9afced50e6c3090bb727264572704942946.zip $GITHUB/UVMS-SpatialModule-DB/archive/f296d9afced50e6c3090bb727264572704942946.zip
unzip -q ~/liquibase/spatial/f296d9afced50e6c3090bb727264572704942946.zip -d ~/liquibase/spatial
cp -r ~/liquibase/spatial/UVMS-SpatialModule-DB-f296d9afced50e6c3090bb727264572704942946/* ~/liquibase/spatial/

# Reporting - Remeber to update this when reporting starts releasing on Github
wget -O ~/liquibase/reporting/reporting-db-$REPORTING_VERSION.zip $GITHUB/UVMS-ReportingModule-DB/archive/reporting-db-$REPORTING_VERSION.zip
unzip -q ~/liquibase/reporting/reporting-db-$REPORTING_VERSION.zip -d ~/liquibase/reporting
cp -r ~/liquibase/reporting/UVMS-ReportingModule-DB-reporting-db-$REPORTING_VERSION/* ~/liquibase/reporting/

# USM - Remeber to update this when usm starts releasing on Github
wget -O ~/liquibase/usm/$USM_VERSION.zip $GITHUB/USM/archive/$USM_VERSION.zip
unzip -q ~/liquibase/usm/$USM_VERSION.zip -d ~/liquibase/usm
cp -r ~/liquibase/usm/USM-$USM_VERSION/* ~/liquibase/usm/


cp usm/pom.xml ~/liquibase/usm/database/liquibase/
cp usm/user.sql ~/liquibase/usm/database/liquibase/changelog/v0.1/boot/
cp usm/USER_T.CSV ~/liquibase/usm/database/liquibase/test/csv/USER_T.csv


cd ~/liquibase/usm/database/liquibase
mvn liquibase:update -Ddb.url=jdbc:postgresql://localhost:5432/db71u -Ddb.user=usm -Ddb.passwd=usm

cd ~/liquibase/asset/LIQUIBASE
mvn liquibase:update -Ppostgres

cd ~/liquibase/audit/LIQUIBASE
mvn liquibase:update -Ppostgres

cd ~/liquibase/config/LIQUIBASE
mvn liquibase:update -Ppostgres

cd ~/liquibase/exchange/LIQUIBASE
mvn liquibase:update -Ppostgres

cd ~/liquibase/movement/LIQUIBASE
mvn liquibase:update -Ppostgres

cd ~/liquibase/mobterm/LIQUIBASE
mvn liquibase:update -Ppostgres

cd ~/liquibase/rules/LIQUIBASE
mvn liquibase:update -Ppostgres

cd ~/liquibase/spatial/LIQUIBASE
#touch schema/initdata/csv/postgres/countries.csv
#touch schema/initdata/csv/postgres/fmz.csv

mvn liquibase:update -Ppostgres,exec -Ddb.url=jdbc:postgresql://localhost:5432/db71u

cd ~/liquibase/reporting/LIQUIBASE
mvn liquibase:update -Ppostgres,exec -Ddb.url=jdbc:postgresql://localhost:5432/db71u

echo "All uvms databases created"