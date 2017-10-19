FROM mdillon/postgis:9.6

COPY setup.sql /docker-entrypoint-initdb.d/

VOLUME ["/docker-entrypoint-initdb.d"]