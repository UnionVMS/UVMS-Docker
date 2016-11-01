FROM uvms/postgres:9.3

# Copy step is important, uvms/postgres runs a script that runs maven in /liquibase/MODULE for all but USM.
# USM liquibase should be located in /liquibase/usm/database/liquibase/

# Asset
ADD $GITHUB/UVMS-AssetModule-DB/archive/swe-dev.zip /liquibase/asset
RUN unzip -q /liquibase/asset/swe-dev.zip -d /liquibase/asset
RUN cp -rf /liquibase/asset/* ../

# Config
ADD $GITHUB/UVMS-ConfigModule-DB/archive/swe-dev.zip /liquibase/config
RUN unzip -q /liquibase/config/swe-dev.zip -d /liquibase/config
RUN cp -rf /liquibase/config/* ../

# Audit
ADD $GITHUB/UVMS-AuditModule-DB/archive/swe-dev.zip /liquibase/audit
RUN unzip -q /liquibase/audit/swe-dev.zip -d /liquibase/audit
RUN cp -rf /liquibase/audit/* ../

# Exchange
ADD $GITHUB/UVMS-ExchangeModule-DB/archive/swe-dev.zip /liquibase/exchange
RUN unzip -q /liquibase/exchange/swe-dev.zip -d /liquibase/exchange
RUN cp -rf /liquibase/exchange/* ../

# Movement
ADD $GITHUB/UVMS-MovementModule-DB/archive/swe-dev.zip /liquibase/movement
RUN unzip -q /liquibase/movement/swe-dev.zip -d /liquibase/movement
RUN cp -rf /liquibase/movement/* ../

# Mobile Terminal
ADD $GITHUB/UVMS-MobileTerminalModule-DB/archive/swe-dev.zip /liquibase/mobterm
RUN unzip -q /liquibase/mobterm/swe-dev.zip -d /liquibase/mobterm
RUN cp -rf /liquibase/mobterm/* ../

# Rules
ADD $GITHUB/UVMS-RulesModule-DB/archive/swe-dev.zip /liquibase/rules
RUN unzip -q /liquibase/rules/swe-dev.zip -d /liquibase/rules
RUN cp -rf /liquibase/rules/* ../

# Spatial
ADD $GITHUB/UVMS-SpatialModule-DB/archive/master.zip /liquibase/spatial
RUN unzip -q /liquibase/spatial/master.zip -d /liquibase/spatial
RUN cp -rf /liquibase/spatial/* ../

# Spatial
ADD $GITHUB/UVMS-ReportingModule-DB/archive/master.zip /liquibase/reporting
RUN unzip -q /liquibase/reporting/master.zip -d /liquibase/reporting
RUN cp -rf /liquibase/reporting/* ../

# USM
ADD $GITHUB/USM/archive/master.zip /liquibase/usm
RUN unzip -q /liquibase/usm/master.zip -d /liquibase/usm
RUN cp -rf /liquibase/usm/* ../

COPY usm/pom.xml /liquibase/usm/database/liquibase/
COPY usm/user.sql /liquibase/usm/database/liquibase/changelog/v0.1/boot/
COPY usm/USER_T.CSV /liquibase/usm/database/liquibase/test/csv/USER_T.csv

RUN ls -l /liquibase/usm/database/liquibase

VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432