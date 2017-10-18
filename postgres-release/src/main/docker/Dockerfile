FROM uvms/postgres-base:${project.version}

ENV GITHUB https://github.com/UnionVMS

COPY sql /var/lib/postgresql/
COPY initDbEnv.sh /var/lib/postgresql/
COPY updateDb.sh /docker-entrypoint-initdb.d/

RUN apt-get update
RUN apt-get install -y unzip
RUN apt-get install -y maven

RUN mkdir -p /home/postgres/.m2/repository
COPY settings.xml /home/postgres/.m2/settings.xml
RUN chmod -R 777 /home/postgres/

#Create temp directories
RUN mkdir -p /liquibase/config \
    && mkdir -p /liquibase/asset \
    && mkdir -p /liquibase/audit \
    && mkdir -p /liquibase/movement \
    && mkdir -p /liquibase/exchange \
    && mkdir -p /liquibase/mobterm \
    && mkdir -p /liquibase/rules \
    && mkdir -p /liquibase/spatial \
    && mkdir -p /liquibase/reporting \
    && mkdir -p /liquibase/usm  \
    && mkdir -p /liquibase/activity \
    && mkdir -p /liquibase/sales \
    && mkdir -p /liquibase/mdr

RUN chown -R postgres:postgres /liquibase

ARG SPATIAL_VERSION=1.0.4
ARG REPORTING_VERSION=1.0.3
ARG USM_VERSION=2.0.0
ARG ACTIVITY_VERSION=1.0.0
ARG MDR_VERSION=1.0.1


# Copy step is important, uvms/postgres runs a script that runs maven in /liquibase/MODULE for all but USM.
# USM liquibase should be located in /liquibase/usm/database/liquibase/


# Spatial - Remeber to update this when spatial starts releasing on Github
ADD https://github.com/UnionVMS/UVMS-SpatialModule-DB/archive/spatial-db-$SPATIAL_VERSION.zip /liquibase/spatial
RUN unzip -q /liquibase/spatial/spatial-db-$SPATIAL_VERSION.zip -d /liquibase/spatial
RUN cp -r /liquibase/spatial/UVMS-SpatialModule-DB-spatial-db-$SPATIAL_VERSION/* /liquibase/spatial/
# Latest commit Dec 9 2016 (contains countries)
#ADD $GITHUB/UVMS-SpatialModule-DB/archive/f296d9afced50e6c3090bb727264572704942946.zip /liquibase/spatial
#RUN unzip -q /liquibase/spatial/f296d9afced50e6c3090bb727264572704942946.zip -d /liquibase/spatial
#RUN cp -r /liquibase/spatial/UVMS-SpatialModule-DB-f296d9afced50e6c3090bb727264572704942946/* /liquibase/spatial/


# Reporting - Remeber to update this when reporting starts releasing on Github
ADD $GITHUB/UVMS-ReportingModule-DB/archive/reporting-db-$REPORTING_VERSION.zip /liquibase/reporting
RUN unzip -q /liquibase/reporting/reporting-db-$REPORTING_VERSION -d /liquibase/reporting
RUN cp -r /liquibase/reporting/UVMS-ReportingModule-DB-reporting-db-$REPORTING_VERSION/* /liquibase/reporting/

# USM - Remeber to update this when usm starts releasing on Github
##ADD $GITHUB/USM/archive/$USM_VERSION.zip /liquibase/usm
##RUN unzip -q /liquibase/usm/$USM_VERSION.zip -d /liquibase/usm
##RUN cp -r /liquibase/usm/USM-$USM_VERSION/* /liquibase/usm/
ADD $GITHUB/USM/archive/b7f78a5fb9411b1f55be6f4f3a1929072ea7c93c.zip /liquibase/usm
RUN unzip -q /liquibase/usm/b7f78a5fb9411b1f55be6f4f3a1929072ea7c93c.zip -d /liquibase/usm
RUN cp -r /liquibase/usm/USM-b7f78a5fb9411b1f55be6f4f3a1929072ea7c93c/* /liquibase/usm/

# Activity
ADD https://github.com/UnionVMS/UVMS-ActivityModule-APP/archive/activity-$ACTIVITY_VERSION.zip /liquibase/activity
RUN unzip -q /liquibase/activity/activity-$ACTIVITY_VERSION.zip -d /liquibase/activity
RUN cp -r /liquibase/activity/UVMS-ActivityModule-APP-activity-$ACTIVITY_VERSION/LIQUIBASE /liquibase/activity/

# MDR
ADD $GITHUB/UVMS-MDRCacheModule-APP/archive/mdr-$MDR_VERSION.zip /liquibase/mdr
RUN unzip -q /liquibase/mdr/mdr-$MDR_VERSION -d /liquibase/mdr
RUN cp -r /liquibase/mdr/UVMS-MDRCacheModule-APP-mdr-$MDR_VERSION/* /liquibase/mdr/


RUN chown -R postgres:postgres /liquibase

COPY usm/pom.xml /liquibase/usm/database/liquibase/
COPY usm/user.sql /liquibase/usm/database/liquibase/changelog/v0.1/boot/
COPY usm/USER_T.CSV /liquibase/usm/database/liquibase/test/csv/USER_T.csv

RUN chmod a+x /var/lib/postgresql/initDbEnv.sh

USER postgres

RUN /var/lib/postgresql/initDbEnv.sh

USER root

RUN chmod -R 777 /home/postgres/
RUN chown -R postgres:postgres /home/postgres/


VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432
