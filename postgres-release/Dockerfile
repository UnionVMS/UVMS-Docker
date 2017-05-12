FROM uvms/postgres-base:3.1.0-SNAPSHOT

ARG ASSET_VERSION=3.0.4
ARG CONFIG_VERSION=3.0.4
ARG AUDIT_VERSION=3.0.4
ARG EXCHANGE_VERSION=3.0.4
ARG MOVEMENT_VERSION=3.0.7
ARG MOBILE_TERMINAL_VERSION=3.0.4
ARG RULES_VERSION=3.0.3
ARG SPATIAL_VERSION=0.5.2
ARG REPORTING_VERSION=0.5.2
ARG USM_VERSION=2.0.0


# Copy step is important, uvms/postgres runs a script that runs maven in /liquibase/MODULE for all but USM.
# USM liquibase should be located in /liquibase/usm/database/liquibase/

# Asset
ADD $GITHUB/UVMS-AssetModule-DB/archive/asset-dbaccess-$ASSET_VERSION.zip /liquibase/asset
RUN unzip -q /liquibase/asset/asset-dbaccess-$ASSET_VERSION.zip -d /liquibase/asset
RUN cp -r /liquibase/asset/UVMS-AssetModule-DB-asset-dbaccess-$ASSET_VERSION/* /liquibase/asset/

# Config
ADD $GITHUB/UVMS-ConfigModule-DB/archive/config-dbaccess-$CONFIG_VERSION.zip /liquibase/config
RUN unzip -q /liquibase/config/config-dbaccess-$CONFIG_VERSION -d /liquibase/config
RUN cp -r /liquibase/config/UVMS-ConfigModule-DB-config-dbaccess-$CONFIG_VERSION/* /liquibase/config/

# Audit
ADD $GITHUB/UVMS-AuditModule-DB/archive/audit-dbaccess-$AUDIT_VERSION.zip /liquibase/audit
RUN unzip -q /liquibase/audit/audit-dbaccess-$AUDIT_VERSION -d /liquibase/audit
RUN cp -r /liquibase/audit/UVMS-AuditModule-DB-audit-dbaccess-$AUDIT_VERSION/* /liquibase/audit/

# Exchange
ADD $GITHUB/UVMS-ExchangeModule-DB/archive/exchange-dbaccess-$EXCHANGE_VERSION.zip /liquibase/exchange
RUN unzip -q /liquibase/exchange/exchange-dbaccess-$EXCHANGE_VERSION.zip -d /liquibase/exchange
RUN cp -r /liquibase/exchange/UVMS-ExchangeModule-DB-exchange-dbaccess-$EXCHANGE_VERSION/* /liquibase/exchange/

# Movement  https://github.com/UnionVMS/
ADD $GITHUB/UVMS-MovementModule-APP/archive/movement-$MOVEMENT_VERSION.zip /liquibase/movement
RUN unzip -q /liquibase/movement/movement-$MOVEMENT_VERSION -d /liquibase/movement
RUN cp -r /liquibase/movement/UVMS-MovementModule-APP-movement-$MOVEMENT_VERSION/* /liquibase/movement/

# Mobile Terminal
ADD $GITHUB/REMOVED_UVMS-MobileTerminalModule-DB/archive/mobileterminal-dbaccess-$MOBILE_TERMINAL_VERSION.zip /liquibase/mobterm
RUN unzip -q /liquibase/mobterm/mobileterminal-dbaccess-$MOBILE_TERMINAL_VERSION -d /liquibase/mobterm
RUN cp -r /liquibase/mobterm/REMOVED_UVMS-MobileTerminalModule-DB-mobileterminal-dbaccess-$MOBILE_TERMINAL_VERSION/* /liquibase/mobterm/

# Rules
ADD $GITHUB/UVMS-RulesModule-DB/archive/rules-dbaccess-$RULES_VERSION.zip /liquibase/rules
RUN unzip -q /liquibase/rules/rules-dbaccess-$RULES_VERSION -d /liquibase/rules
RUN cp -r /liquibase/rules/UVMS-RulesModule-DB-rules-dbaccess-$RULES_VERSION/* /liquibase/rules/

# Spatial - Remeber to update this when spatial starts releasing on Github
##ADD $GITHUB/UVMS-SpatialModule-DB/archive/UVMS-SpatialModule-DB-$SPATIAL_VERSION.zip /liquibase/spatial
##RUN unzip -q /liquibase/spatial/spatial-dbaccess-$SPATIAL_VERSION -d /liquibase/spatial
##RUN cp -r /liquibase/spatial/UVMS-SpatialModule-DB-spatial-dbaccess-$SPATIAL_VERSION/* /liquibase/spatial/
#No release tags for spatial, commit using 0.5.2 commit
#ADD $GITHUB/UVMS-SpatialModule-DB/archive/418c616e9daf5c1274097fb32216b18b7f3a019e.zip /liquibase/spatial
#RUN unzip -q /liquibase/spatial/418c616e9daf5c1274097fb32216b18b7f3a019e.zip -d /liquibase/spatial
#RUN cp -r /liquibase/spatial/UVMS-SpatialModule-DB-418c616e9daf5c1274097fb32216b18b7f3a019e/* /liquibase/spatial/
# Latest commit Dec 9 2016 
ADD $GITHUB/UVMS-SpatialModule-DB/archive/f296d9afced50e6c3090bb727264572704942946.zip /liquibase/spatial
RUN unzip -q /liquibase/spatial/f296d9afced50e6c3090bb727264572704942946.zip -d /liquibase/spatial
RUN cp -r /liquibase/spatial/UVMS-SpatialModule-DB-f296d9afced50e6c3090bb727264572704942946/* /liquibase/spatial/

# Reporting - Remeber to update this when reporting starts releasing on Github
ADD $GITHUB/UVMS-ReportingModule-DB/archive/reporting-db-$REPORTING_VERSION.zip /liquibase/reporting
RUN unzip -q /liquibase/reporting/reporting-db-$REPORTING_VERSION -d /liquibase/reporting
RUN cp -r /liquibase/reporting/UVMS-ReportingModule-DB-reporting-db-$REPORTING_VERSION/* /liquibase/reporting/

# USM - Remeber to update this when usm starts releasing on Github
ADD $GITHUB/USM/archive/$USM_VERSION.zip /liquibase/usm
RUN unzip -q /liquibase/usm/$USM_VERSION.zip -d /liquibase/usm
RUN cp -r /liquibase/usm/USM-$USM_VERSION/* /liquibase/usm/

RUN chown -R postgres:postgres /liquibase

COPY usm/pom.xml /liquibase/usm/database/liquibase/
COPY usm/user.sql /liquibase/usm/database/liquibase/changelog/v0.1/boot/
COPY usm/USER_T.CSV /liquibase/usm/database/liquibase/test/csv/USER_T.csv

#RUN ls -l /docker-entrypoint-initdb.d

VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432