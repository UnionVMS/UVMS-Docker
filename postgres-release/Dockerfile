FROM uvms/postgres:9.3

ARG VERSION=2.1.3

COPY download.sh /liquibase/

# Asset
WORKDIR /liquibase
RUN /liquibase/download.sh UVMS-AssetModule-DB dev asset
RUN /liquibase/download.sh UVMS-AuditModule-DB dev audit
RUN /liquibase/download.sh UVMS-ConfigModule-DB dev config
RUN /liquibase/download.sh UVMS-ExchangeModule-DB dev exchange
RUN /liquibase/download.sh UVMS-MobileTerminalModule-DB dev mobterm
RUN /liquibase/download.sh UVMS-MovementModule-DB dev movement
RUN /liquibase/download.sh UVMS-RulesModule-DB dev rules
RUN /liquibase/download.sh UVMS-SpatialModule-DB dev spatial
RUN /liquibase/download.sh UVMS-ReportingModule-DB dev reporting
RUN /liquibase/download.sh USM master usm
RUN ls -l

COPY usm/pom.xml /liquibase/usm/database/liquibase/
COPY usm/user.sql /liquibase/usm/database/liquibase/changelog/v0.1/boot/
COPY usm/USER_T.CSV /liquibase/usm/database/liquibase/test/csv/USER_T.csv
