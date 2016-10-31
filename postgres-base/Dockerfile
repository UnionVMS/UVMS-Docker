FROM mdillon/postgis:9.3

ENV GITHUB https://github.com/UnionVMS
ENV TARGET target/liquibase/migrate.sql
ENV LIQUIBASE liquibase:updateSQL -f pom.xml -Ppostgres -Ddb.url=offline:postgresql?version=9.3&outputLiquibaseSql=true

RUN apt-get update
RUN apt-get install -y unzip
RUN apt-get install -y maven

COPY settings.xml /root/.m2/settings.xml

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
    && mkdir -p /liquibase/usm

COPY setup.sql /docker-entrypoint-initdb.d/

VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432