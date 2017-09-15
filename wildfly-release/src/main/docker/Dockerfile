FROM uvms/wildfly-base:${project.version}

ARG checkDependency=true
ENV DEPENDENCY_CHECK $checkDependency
RUN echo "Dependency check value is : $DEPENDENCY_CHECK"
 
COPY module-download-pom.xml /opt/jboss/
RUN mkdir -p /opt/jboss/jboss-deployment
RUN mkdir -p /opt/jboss/jboss-deployment/asset
RUN mkdir -p /opt/jboss/jboss-deployment/audit
RUN mkdir -p /opt/jboss/jboss-deployment/config
RUN mkdir -p /opt/jboss/jboss-deployment/exchange
RUN mkdir -p /opt/jboss/jboss-deployment/general
RUN mkdir -p /opt/jboss/jboss-deployment/mobile
RUN mkdir -p /opt/jboss/jboss-deployment/movement
RUN mkdir -p /opt/jboss/jboss-deployment/reporting
RUN mkdir -p /opt/jboss/jboss-deployment/rules
RUN mkdir -p /opt/jboss/jboss-deployment/spatial
RUN mkdir -p /opt/jboss/jboss-deployment/activity
RUN mkdir -p /opt/jboss/jboss-deployment/mdr
RUN mkdir -p /opt/jboss/jboss-deployment/user
RUN mkdir -p /opt/jboss/jboss-deployment/web-app
RUN mkdir -p /opt/jboss/jboss-deployment/plugins
RUN mkdir -p /opt/jboss/jboss-deployment/fluxBridge


COPY jboss-deployment/add-jboss-all-to-ear.sh /opt/jboss/jboss-deployment/
COPY jboss-deployment/asset/jboss-all.xml /opt/jboss/jboss-deployment/asset/
COPY jboss-deployment/audit/jboss-all.xml /opt/jboss/jboss-deployment/audit/
COPY jboss-deployment/config/jboss-all.xml /opt/jboss/jboss-deployment/config/
COPY jboss-deployment/exchange/jboss-all.xml /opt/jboss/jboss-deployment/exchange/
COPY jboss-deployment/general/jboss-all.xml /opt/jboss/jboss-deployment/general/
COPY jboss-deployment/mobile/jboss-all.xml /opt/jboss/jboss-deployment/mobile/
COPY jboss-deployment/movement/jboss-all.xml /opt/jboss/jboss-deployment/movement/
COPY jboss-deployment/reporting/jboss-all.xml /opt/jboss/jboss-deployment/reporting/
COPY jboss-deployment/rules/jboss-all.xml /opt/jboss/jboss-deployment/rules/
COPY jboss-deployment/spatial/jboss-all.xml /opt/jboss/jboss-deployment/spatial/
COPY jboss-deployment/activity/jboss-all.xml /opt/jboss/jboss-deployment/activity/
COPY jboss-deployment/mdr/jboss-all.xml /opt/jboss/jboss-deployment/mdr/
COPY jboss-deployment/user/jboss-all.xml /opt/jboss/jboss-deployment/user/
COPY jboss-deployment/plugins/jboss-all.xml /opt/jboss/jboss-deployment/plugins/
COPY jboss-deployment/web-app/jboss-deployment-structure.xml /opt/jboss/jboss-deployment/web-app/
COPY jboss-deployment/web-app/jboss-all.xml /opt/jboss/jboss-deployment/web-app/

# Folder containing connection properties of jmsScheduler bridge app, needed for the plugins to dispatch messages to FLUX TL
COPY FluxBridgeProperties/bridge.properties /opt/jboss/jboss-deployment/fluxBridge
COPY FluxBridgeProperties/bridge-queues.properties /opt/jboss/jboss-deployment/fluxBridge

 
USER root
RUN mvn dependency:copy-dependencies -DoutputDirectory=$DEPLOYMENTS -f module-download-pom.xml -q
RUN chmod 777 /opt/jboss/wildfly/standalone/deployments/*.ear
RUN chmod 777 /opt/jboss/wildfly/standalone/deployments/
RUN chmod a+x /opt/jboss/jboss-deployment/add-jboss-all-to-ear.sh
RUN if $DEPENDENCY_CHECK -eq "true"; then /opt/jboss/jboss-deployment/add-jboss-all-to-ear.sh; fi
RUN chmod 755 /opt/jboss/wildfly/standalone/deployments/*.ear
RUN chown -R jboss:jboss /opt/jboss/wildfly/standalone/deployments/
USER jboss

USER root
RUN mkdir -p /app/indexes/mdr
RUN chmod 777 /app/indexes/mdr
RUN rm -rf /root/.m2
USER jboss