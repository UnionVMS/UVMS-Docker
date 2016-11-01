# Postgres Base
Dockerfile for basic PostgreSQL container. This container is used as base for postgres-release and includes configuration of Postgres.

## Creating tags
If UVMS moves to a new version of PostgreSQL there are a few steps to update this container.

1. Change "FROM mdillon/postgis:9.3" in Dockerfile to the correct version of PostgreSQL.
2. docker build -t uvms/postgres:<postgres version> .
3. docker push uvms/postgres:<postgres version>

If Docker authentication is required, run docker login and follow the instructions, then re run step 2 above.