FROM uvms/wildfly-base:3.1.0-SNAPSHOT

COPY module-download-pom.xml /opt/jboss/
USER root
RUN mvn dependency:copy-dependencies -DoutputDirectory=$DEPLOYMENTS -f module-download-pom.xml -q
USER jboss

USER root
RUN mv /root/.m2/settings.xml /root/settings.xml
RUN rm -r /root/.m2
RUN mkdir /root/.m2 && mv /root/settings.xml /root/.m2/settings.xml
RUN ls -l /root/.m2/
USER jboss

RUN ls -l $DEPLOYMENTS