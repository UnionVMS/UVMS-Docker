#!bin/sh
JAVA_HOME=/usr/lib/jvm/default-java
export JAVA_HOME

touch /tmp/ojdbc6-11.2.0.4.jar
mvn install:install-file -Dfile=/tmp/ojdbc6-11.2.0.4.jar -DgroupId=com.oracle  -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true

cd /liquibase/usm/database/liquibase
mvn liquibase:update -Ppostgres -Dliquibase.skip=false

cd /liquibase/asset/LIQUIBASE
mvn liquibase:update -Ppostgres -Dliquibase.skip=false

cd /liquibase/audit/LIQUIBASE
mvn liquibase:update -Ppostgres -Dliquibase.skip=false

cd /liquibase/config/LIQUIBASE
mvn liquibase:update -Ppostgres -Dliquibase.skip=false

cd /liquibase/exchange/LIQUIBASE
mvn liquibase:update -Ppostgres -Dliquibase.skip=false

cd /liquibase/movement/LIQUIBASE
mvn liquibase:update -Ppostgres -Dliquibase.skip=false

cd /liquibase/mobterm/LIQUIBASE
mvn liquibase:update -Ppostgres -Dliquibase.skip=false

cd /liquibase/rules/LIQUIBASE
mvn liquibase:update -Ppostgres -Dliquibase.skip=false

cd /liquibase/spatial/LIQUIBASE

mvn liquibase:update -Ppostgres -Ddb.url=jdbc:postgresql://localhost:5432/db71u -Dliquibase.skip=false

cd /liquibase/reporting/LIQUIBASE
mvn liquibase:update -Ppostgres -Ddb.url=jdbc:postgresql://localhost:5432/db71u -Dliquibase.skip=false

echo "All uvms databases scripts prepared"
