FROM mdillon/postgis:9.3

ENV GITHUB https://github.com/UnionVMS

RUN apt-get update
RUN apt-get install -y unzip
RUN apt-get install -y maven

RUN mkdir -p /home/postgres/.m2
COPY settings.xml /root/.m2/settings.xml
RUN chown -R postgres:postgres /home/postgres/

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
RUN chown -R postgres:postgres /liquibase

COPY setup.sql /docker-entrypoint-initdb.d/
COPY updateDb.sh /docker-entrypoint-initdb.d/

RUN echo "max_prepared_transactions = 100" >> $PGDATA/postgresql.conf

VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432