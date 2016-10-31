FROM uvms/postgres:9.3

ENV VERSION 2.1.1

# Asset
ADD $GITHUB/UVMS-AssetModule-DB/archive/asset-dbaccess-$VERSION.zip /liquibase/asset
RUN unzip -q /liquibase/asset/asset-dbaccess-$VERSION.zip -d /liquibase/asset

# Config
ADD $GITHUB/UVMS-ConfigModule-DB/archive/config-dbaccess-$VERSION.zip /liquibase/config
RUN unzip -q /liquibase/config/config-dbaccess-$VERSION -d /liquibase/config

# Audit
ADD $GITHUB/UVMS-AuditModule-DB/archive/audit-dbaccess-$VERSION.zip /liquibase/audit
RUN unzip -q /liquibase/audit/audit-dbaccess-$VERSION -d /liquibase/audit

# Exchange
ADD $GITHUB/UVMS-ExchangeModule-DB/archive/exchange-dbaccess-$VERSION.zip /liquibase/exchange
RUN unzip -q /liquibase/exchange/exchange-dbaccess-$VERSION.zip -d /liquibase/exchange

# Movement
ADD $GITHUB/UVMS-MovementModule-DB/archive/movement-dbaccess-$VERSION.zip /liquibase/movement
RUN unzip -q /liquibase/movement/movement-dbaccess-$VERSION -d /liquibase/movement

# Mobile Terminal
ADD $GITHUB/UVMS-MobileTerminalModule-DB/archive/mobileterminal-dbaccess-$VERSION.zip /liquibase/mobterm
RUN unzip -q /liquibase/mobterm/mobileterminal-dbaccess-$VERSION -d /liquibase/mobterm

# Rules
ADD $GITHUB/UVMS-RulesModule-DB/archive/rules-dbaccess-$VERSION.zip /liquibase/rules
RUN unzip -q /liquibase/rules/rules-dbaccess-$VERSION -d /liquibase/rules

# Spatial - Remeber to update this when spatial starts releasing on Github
ADD $GITHUB/UVMS-SpatialModule-DB/archive/master.zip /liquibase/spatial
RUN unzip -q /liquibase/spatial/master.zip -d /liquibase/spatial

# Reporting - Remeber to update this when reporting starts releasing on Github
ADD $GITHUB/UVMS-ReportingModule-DB/archive/master.zip /liquibase/reporting
RUN unzip -q /liquibase/reporting/master.zip -d /liquibase/reporting

# USM - Remeber to update this when usm starts releasing on Github
ADD $GITHUB/USM/archive/2.0.0.zip /liquibase/usm
RUN unzip -q /liquibase/usm/2.0.0.zip -d /liquibase/usm

COPY usm/pom.xml /liquibase/usm/USM-master/database/liquibase/
COPY usm/user.sql /liquibase/usm/USM-master/database/liquibase/changelog/v0.1/boot/
COPY usm/USER_T.CSV /liquibase/usm/USM-master/database/liquibase/test/csv/USER_T.csv

COPY updateDb.sh /docker-entrypoint-initdb.d/

#RUN ls -l /docker-entrypoint-initdb.d

VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432