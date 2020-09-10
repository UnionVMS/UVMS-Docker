FROM postgis/postgis:12-2.5

ENV POSTGRES_PASSWORD postgres

COPY setup.sql /docker-entrypoint-initdb.d/

VOLUME ["/docker-entrypoint-initdb.d"]