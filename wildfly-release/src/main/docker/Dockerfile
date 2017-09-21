FROM uvms/wildfly-base:${project.version}

COPY deployments /opt/jboss/wildfly/standalone/deployments/
 
USER root
RUN chown -R jboss:jboss /opt/jboss/wildfly/standalone/deployments/
RUN mkdir -p /opt/jboss/jboss-deployment/fluxBridge
COPY FluxBridgeProperties/bridge.properties /opt/jboss/jboss-deployment/fluxBridge
COPY FluxBridgeProperties/bridge-queues.properties /opt/jboss/jboss-deployment/fluxBridge
RUN chown -R jboss:jboss /opt/jboss/jboss-deployment/fluxBridge
USER jboss

USER root
RUN mkdir -p /app/indexes/mdr
RUN chmod 777 /app/indexes/mdr
USER jboss