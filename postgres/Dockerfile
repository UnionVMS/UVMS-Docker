FROM uvms/postgres:9.3

# Copy step is important, uvms/postgres runs a script that runs maven in /liquibase/MODULE for all but USM.
# USM liquibase should be located in /liquibase/usm/database/liquibase/

# Asset
ADD $GITHUB/UVMS-AssetModule-DB/archive/dev.zip /liquibase/asset
RUN unzip -q /liquibase/asset/dev.zip -d /liquibase/asset
RUN cp -r /liquibase/asset/UVMS-AssetModule-DB-dev/* /liquibase/asset/

# Config
ADD $GITHUB/UVMS-ConfigModule-DB/archive/dev.zip /liquibase/config
RUN unzip -q /liquibase/config/dev.zip -d /liquibase/config
RUN cp -r /liquibase/config/UVMS-ConfigModule-DB-dev/* /liquibase/config/

# Audit
ADD $GITHUB/UVMS-AuditModule-DB/archive/dev.zip /liquibase/audit
RUN unzip -q /liquibase/audit/dev.zip -d /liquibase/audit
RUN cp -r /liquibase/audit/UVMS-AuditModule-DB-dev/* /liquibase/audit/

# Exchange
ADD $GITHUB/UVMS-ExchangeModule-DB/archive/dev.zip /liquibase/exchange
RUN unzip -q /liquibase/exchange/dev.zip -d /liquibase/exchange
RUN cp -r /liquibase/exchange/UVMS-ExchangeModule-DB-dev/* /liquibase/exchange/

# Movement
ADD $GITHUB/UVMS-MovementModule-DB/archive/dev.zip /liquibase/movement
RUN unzip -q /liquibase/movement/dev.zip -d /liquibase/movement
RUN cp -r /liquibase/movement/UVMS-MovementModule-DB-dev/* /liquibase/movement/

# Mobile Terminal
ADD $GITHUB/UVMS-MobileTerminalModule-DB/archive/dev.zip /liquibase/mobterm
RUN unzip -q /liquibase/mobterm/dev.zip -d /liquibase/mobterm
RUN cp -r /liquibase/mobterm/UVMS-MobileTerminalModule-DB-dev/* /liquibase/mobterm/

# Rules
ADD $GITHUB/UVMS-RulesModule-DB/archive/dev.zip /liquibase/rules
RUN unzip -q /liquibase/rules/dev.zip -d /liquibase/rules
RUN cp -r /liquibase/rules/UVMS-RulesModule-DB-dev/* /liquibase/rules/

# Spatial
ADD $GITHUB/UVMS-SpatialModule-DB/archive/master.zip /liquibase/spatial
RUN unzip -q /liquibase/spatial/master.zip -d /liquibase/spatial
RUN cp -r /liquibase/spatial/UVMS-SpatialModule-DB-master/* /liquibase/spatial/

# Spatial
ADD $GITHUB/UVMS-ReportingModule-DB/archive/master.zip /liquibase/reporting
RUN unzip -q /liquibase/reporting/master.zip -d /liquibase/reporting
RUN cp -r /liquibase/reporting/UVMS-ReportingModule-DB-master/* /liquibase/reporting/

# USM
ADD $GITHUB/USM/archive/master.zip /liquibase/usm
RUN unzip -q /liquibase/usm/master.zip -d /liquibase/usm
RUN cp -r /liquibase/usm/USM-master/* /liquibase/usm/

COPY usm/pom.xml /liquibase/usm/database/liquibase/
COPY usm/user.sql /liquibase/usm/database/liquibase/changelog/v0.1/boot/
COPY usm/USER_T.CSV /liquibase/usm/database/liquibase/test/csv/USER_T.csv

WORKDIR /liquibase
RUN mvn dependency:resolve -f asset/LIQUIBASE/pom.xml
RUN mvn dependency:resolve -f config/LIQUIBASE/pom.xml
RUN mvn dependency:resolve -f audit/LIQUIBASE/pom.xml
RUN mvn dependency:resolve -f exchange/LIQUIBASE/pom.xml
RUN mvn dependency:resolve -f movement/LIQUIBASE/pom.xml
RUN mvn dependency:resolve -f mobterm/LIQUIBASE/pom.xml
RUN mvn dependency:resolve -f rules/LIQUIBASE/pom.xml
RUN mvn dependency:resolve -f reporting/LIQUIBASE/pom.xml
RUN mvn dependency:resolve -f spatial/LIQUIBASE/pom.xml
RUN mvn dependency:resolve -f usm/database/liquibase/pom.xml

VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432