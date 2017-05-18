#!bin/sh
JAVA_HOME=/usr/lib/jvm/default-java
export JAVA_HOME

whoami

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

echo "All uvms databases created"