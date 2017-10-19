FROM mdillon/postgis:9.6-alpine

COPY setup.sql /docker-entrypoint-initdb.d/

VOLUME ["/docker-entrypoint-initdb.d"]