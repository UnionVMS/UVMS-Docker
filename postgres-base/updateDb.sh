#!bin/sh
JAVA_HOME=/usr/lib/jvm/default-java
export JAVA_HOME

touch /tmp/ojdbc6-11.2.0.4.jar
mvn install:install-file -Dfile=/tmp/ojdbc6-11.2.0.4.jar -DgroupId=com.oracle  -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true


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
#touch schema/initdata/csv/postgres/countries.csv
#touch schema/initdata/csv/postgres/fmz.csv

mvn liquibase:update -Ppostgres,exec -Ddb.url=jdbc:postgresql://localhost:5432/db71u

cd /liquibase/reporting/LIQUIBASE
mvn liquibase:update -Ppostgres,exec -Ddb.url=jdbc:postgresql://localhost:5432/db71u

echo "All uvms databases created"