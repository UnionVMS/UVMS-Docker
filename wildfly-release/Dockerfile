FROM uvms/wildfly-base:3.1.0-SNAPSHOT

COPY module-download-pom.xml /opt/jboss/
COPY jboss-all.xml /opt/jboss/
COPY add-jboss-all-to-ear.sh /opt/jboss/
 
USER root
RUN mvn dependency:copy-dependencies -DoutputDirectory=$DEPLOYMENTS -f module-download-pom.xml -q
RUN chown jboss:jboss /opt/jboss/add-jboss-all-to-ear.sh
RUN chmod 777 /opt/jboss/wildfly/standalone/deployments/*.ear
RUN chmod a+x /opt/jboss/add-jboss-all-to-ear.sh
RUN /opt/jboss/add-jboss-all-to-ear.sh
RUN chmod 755 /opt/jboss/wildfly/standalone/deployments/*.ear
RUN chown -R jboss:jboss /opt/jboss/wildfly/standalone/deployments/
USER jboss

USER root
RUN mv /root/.m2/settings.xml /root/settings.xml
RUN rm -r /root/.m2
RUN mkdir /root/.m2 && mv /root/settings.xml /root/.m2/settings.xml
RUN ls -l /root/.m2/
USER jboss

RUN ls -l $DEPLOYMENTS