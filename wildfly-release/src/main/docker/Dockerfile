FROM uvms/wildfly-base:${project.version}

COPY deployments /opt/jboss/wildfly/standalone/deployments/
 
USER root
RUN chown -R jboss:jboss /opt/jboss/wildfly/standalone/deployments/
USER jboss

USER root
RUN mkdir -p /app/indexes/mdr
RUN chmod 777 /app/indexes/mdr
USER jboss