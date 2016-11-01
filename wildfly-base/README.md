# Wildfly Base
Dockerfile for basic wildfly container. This container is used as base for wildfly-release and includes configuration of Wildfly.

## Creating tags
If UVMS moves to a new version of Wildfly there are a few steps to update this container.

1. Change "FROM jboss/wildfly:8.2.0.Final" in Dockerfile to the correct version of Wildfly.
2. Change "ENV WILDFLY_VERSION 8.2.0.Final" in Dockerfile to the correct version of Wildfly.
  *. (Optional) Update any other versions, i.e. Hibernate spatial
3. docker build -t uvms/wildfly:<wildfly version> .
4. docker push uvms/wildfly:<wildfly version>

If Docker authentication is required, run docker login and follow the instructions, then re run step 2 above.