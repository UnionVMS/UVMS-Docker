FROM uvms/postgres:9.3

# Asset
ADD $GITHUB/UVMS-AssetModule-DB/archive/swe-dev.zip /liquibase/asset
RUN unzip -q /liquibase/asset/swe-dev.zip -d /liquibase/asset

# Config
ADD $GITHUB/UVMS-ConfigModule-DB/archive/swe-dev.zip /liquibase/config
RUN unzip -q /liquibase/config/swe-dev.zip -d /liquibase/config

# Audit
ADD $GITHUB/UVMS-AuditModule-DB/archive/swe-dev.zip /liquibase/audit
RUN unzip -q /liquibase/audit/swe-dev.zip -d /liquibase/audit

# Exchange
ADD $GITHUB/UVMS-ExchangeModule-DB/archive/swe-dev.zip /liquibase/exchange
RUN unzip -q /liquibase/exchange/swe-dev.zip -d /liquibase/exchange

# Movement
ADD $GITHUB/UVMS-MovementModule-DB/archive/swe-dev.zip /liquibase/movement
RUN unzip -q /liquibase/movement/swe-dev.zip -d /liquibase/movement

# Mobile Terminal
ADD $GITHUB/UVMS-MobileTerminalModule-DB/archive/swe-dev.zip /liquibase/mobterm
RUN unzip -q /liquibase/mobterm/swe-dev.zip -d /liquibase/mobterm

# Rules
ADD $GITHUB/UVMS-RulesModule-DB/archive/swe-dev.zip /liquibase/rules
RUN unzip -q /liquibase/rules/swe-dev.zip -d /liquibase/rules

# Spatial
ADD $GITHUB/UVMS-SpatialModule-DB/archive/master.zip /liquibase/spatial
RUN unzip -q /liquibase/spatial/master.zip -d /liquibase/spatial

# Spatial
ADD $GITHUB/UVMS-ReportingModule-DB/archive/master.zip /liquibase/reporting
RUN unzip -q /liquibase/reporting/master.zip -d /liquibase/reporting

# USM
ADD $GITHUB/USM/archive/master.zip /liquibase/usm
RUN unzip -q /liquibase/usm/master.zip -d /liquibase/usm

COPY usm/pom.xml /liquibase/usm/USM-master/database/liquibase/
COPY usm/user.sql /liquibase/usm/USM-master/database/liquibase/changelog/v0.1/boot/
COPY usm/USER_T.CSV /liquibase/usm/USM-master/database/liquibase/test/csv/USER_T.csv

COPY updateDb.sh /docker-entrypoint-initdb.d/

#RUN ls -l /docker-entrypoint-initdb.d

VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432