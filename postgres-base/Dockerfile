FROM mdillon/postgis:9.5

COPY setup.sql /docker-entrypoint-initdb.d/

RUN echo "max_prepared_transactions = 100" >> $PGDATA/postgresql.conf

VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432