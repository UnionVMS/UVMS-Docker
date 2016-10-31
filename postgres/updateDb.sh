#!bin/sh
cd /liquibase/asset/UVMS-AssetModule-DB-swe-dev/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/audit/UVMS-AuditModule-DB-swe-dev/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/config/UVMS-ConfigModule-DB-swe-dev/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/exchange/UVMS-ExchangeModule-DB-swe-dev/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/movement/UVMS-MovementModule-DB-swe-dev/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/mobterm/UVMS-MobileTerminalModule-DB-swe-dev/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/rules/UVMS-RulesModule-DB-swe-dev/LIQUIBASE
mvn liquibase:update -Ppostgres

cd /liquibase/spatial/UVMS-SpatialModule-DB-master/LIQUIBASE
mvn liquibase:update -Ppostgres -Ddb.url=jdbc:postgresql://localhost:5432/db71u

cd /liquibase/reporting/UVMS-ReportingModule-DB-master/LIQUIBASE
mvn liquibase:update -Ppostgres -Ddb.url=jdbc:postgresql://localhost:5432/db71u

cd /liquibase/usm/USM-master/LIQUIBASE
mvn liquibase:update -Ddb.url=jdbc:postgresql://localhost:5432/db71u -Ddb.user=usm -Ddb.passwd=usm