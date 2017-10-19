FROM uvms/postgres-base:${project.version}

COPY sql /var/lib/postgresql/
COPY updateDb.sh /docker-entrypoint-initdb.d/

VOLUME ["/docker-entrypoint-initdb.d"]

EXPOSE 5432
