#!bin/sh
JAVA_HOME=/usr/lib/jvm/default-java
export JAVA_HOME

touch /tmp/ojdbc6-11.2.0.4.jar
mvn install:install-file -Dfile=/tmp/ojdbc6-11.2.0.4.jar -DgroupId=com.oracle  -DartifactId=ojdbc6 -Dversion=11.2.0.4 -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true

cd /liquibase/usm/database/liquibase
mvn clean verify -Ppostgres

cd /liquibase/asset/LIQUIBASE
mvn clean verify -Ppostgres

cd /liquibase/audit/LIQUIBASE
mvn clean verify -Ppostgres

cd /liquibase/config/LIQUIBASE
mvn clean verify -Ppostgres

cd /liquibase/exchange/LIQUIBASE
mvn clean verify -Ppostgres

cd /liquibase/movement/LIQUIBASE
mvn clean verify -Ppostgres

cd /liquibase/mobterm/LIQUIBASE
mvn clean verify -Ppostgres

cd /liquibase/rules/LIQUIBASE
mvn clean verify -Ppostgres

cd /liquibase/spatial/LIQUIBASE

mvn clean verify -Ppostgres

cd /liquibase/reporting/LIQUIBASE
mvn clean verify -Ppostgres

echo "All uvms databases scripts prepared"