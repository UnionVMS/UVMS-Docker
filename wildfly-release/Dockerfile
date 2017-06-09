FROM uvms/wildfly-base:3.2.1-SNAPSHOT

COPY module-download-pom.xml /opt/jboss/
RUN mkdir -p /opt/jboss/jboss-deployment
RUN mkdir -p /opt/jboss/jboss-deployment/asset
RUN mkdir -p /opt/jboss/jboss-deployment/audit
RUN mkdir -p /opt/jboss/jboss-deployment/config
RUN mkdir -p /opt/jboss/jboss-deployment/config-db
RUN mkdir -p /opt/jboss/jboss-deployment/exchange
RUN mkdir -p /opt/jboss/jboss-deployment/general
RUN mkdir -p /opt/jboss/jboss-deployment/mobile
RUN mkdir -p /opt/jboss/jboss-deployment/movement
RUN mkdir -p /opt/jboss/jboss-deployment/reporting
RUN mkdir -p /opt/jboss/jboss-deployment/rules
RUN mkdir -p /opt/jboss/jboss-deployment/spatial
RUN mkdir -p /opt/jboss/jboss-deployment/user
RUN mkdir -p /opt/jboss/jboss-deployment/web-app
RUN mkdir -p /opt/jboss/jboss-deployment/plugins


COPY jboss-deployment/add-jboss-all-to-ear.sh /opt/jboss/jboss-deployment/
COPY jboss-deployment/asset/jboss-all.xml /opt/jboss/jboss-deployment/asset/
COPY jboss-deployment/audit/jboss-all.xml /opt/jboss/jboss-deployment/audit/
COPY jboss-deployment/config/jboss-all.xml /opt/jboss/jboss-deployment/config/
COPY jboss-deployment/config-db/jboss-all.xml /opt/jboss/jboss-deployment/config-db/
COPY jboss-deployment/exchange/jboss-all.xml /opt/jboss/jboss-deployment/exchange/
COPY jboss-deployment/general/jboss-all.xml /opt/jboss/jboss-deployment/general/
COPY jboss-deployment/mobile/jboss-all.xml /opt/jboss/jboss-deployment/mobile/
COPY jboss-deployment/movement/jboss-all.xml /opt/jboss/jboss-deployment/movement/
COPY jboss-deployment/reporting/jboss-all.xml /opt/jboss/jboss-deployment/reporting/
COPY jboss-deployment/rules/jboss-all.xml /opt/jboss/jboss-deployment/rules/
COPY jboss-deployment/spatial/jboss-all.xml /opt/jboss/jboss-deployment/spatial/
COPY jboss-deployment/user/jboss-all.xml /opt/jboss/jboss-deployment/user/
COPY jboss-deployment/plugins/jboss-all.xml /opt/jboss/jboss-deployment/plugins/
COPY jboss-deployment/web-app/jboss-deployment-structure.xml /opt/jboss/jboss-deployment/web-app/
COPY jboss-deployment/web-app/jboss-all.xml /opt/jboss/jboss-deployment/web-app/

 
USER root
RUN mvn dependency:copy-dependencies -DoutputDirectory=$DEPLOYMENTS -f module-download-pom.xml -q
#RUN chown -R jboss:jboss /opt/jboss/jboss-deployment/add-jboss-all-to-ear.sh
RUN chown -R jboss:jboss /opt/jboss/
RUN chmod 777 /opt/jboss/wildfly/standalone/deployments/*.ear
RUN chmod a+x /opt/jboss/jboss-deployment/add-jboss-all-to-ear.sh
RUN /opt/jboss/jboss-deployment/add-jboss-all-to-ear.sh
RUN chmod 755 /opt/jboss/wildfly/standalone/deployments/*.ear
RUN chown -R jboss:jboss /opt/jboss/wildfly/standalone/deployments/
RUN chmod 755 /opt/jboss/wildfly/standalone/log/
RUN chown -R jboss:jboss /opt/jboss/wildfly/standalone/log/
USER jboss

USER root
RUN mv /root/.m2/settings.xml /root/settings.xml
RUN rm -r /root/.m2
RUN mkdir /root/.m2 && mv /root/settings.xml /root/.m2/settings.xml
RUN ls -l /root/.m2/
USER jboss

RUN ls -l $DEPLOYMENTS