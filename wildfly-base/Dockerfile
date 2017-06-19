FROM jboss/wildfly:8.2.0.Final

ENV WILDFLY_VERSION 8.2.0.Final
ENV HIBERNATE_VERSION 4.3.11.Final
ENV HIBERNATE_SPATIAL_VERSION 4.3
ENV JTS_VERSION 1.13
ENV POSTGIS_VERSION 1.5.3
ENV POSTGRES_VERSION 9.3-1102-jdbc4
ENV ACTIVEMQ_VERSION 5.13.2

ENV NEXUS http://nexus.focus.fish/nexus/service/local/artifact/maven/content
ENV HIBERNATE_MODULE $JBOSS_HOME/modules/system/layers/base/org/hibernate/main
ENV POSTGRES_MODULE $JBOSS_HOME/modules/org/postgresql/main
ENV DEPLOYMENTS $JBOSS_HOME/standalone/deployments
ENV PGPASSWORD=postgres

# PostGis
COPY hibernate/module.xml $HIBERNATE_MODULE

#RUN curl -o $HIBERNATE_MODULE/hibernate-core-$HIBERNATE_VERSION.jar "http://central.maven.org/maven2/org/hibernate/hibernate-core/$HIBERNATE_VERSION/hibernate-core-$HIBERNATE_VERSION.jar"
#RUN curl -o $HIBERNATE_MODULE/hibernate-envers-$HIBERNATE_VERSION.jar "http://central.maven.org/maven2/org/hibernate/hibernate-envers/$HIBERNATE_VERSION/hibernate-envers-$HIBERNATE_VERSION.jar"
#RUN curl -o $HIBERNATE_MODULE/hibernate-entitymanager-$HIBERNATE_VERSION.jar "http://central.maven.org/maven2/org/hibernate/hibernate-entitymanager/$HIBERNATE_VERSION/hibernate-entitymanager-$HIBERNATE_VERSION.jar"
#RUN curl -o $HIBERNATE_MODULE/hibernate-infinispan-$HIBERNATE_VERSION.Final.jar "http://central.maven.org/maven2/org/hibernate/hibernate-infinispan/$HIBERNATE_VERSION/hibernate-infinispan-$HIBERNATE_VERSION.jar"

#RUN curl -o $HIBERNATE_MODULE/hibernate-spatial-$HIBERNATE_SPATIAL_VERSION.jar "http://www.hibernatespatial.org/repository/org/hibernate/hibernate-spatial/$HIBERNATE_SPATIAL_VERSION/hibernate-spatial-$HIBERNATE_SPATIAL_VERSION.jar"
RUN curl -o $HIBERNATE_MODULE/hibernate-spatial-$HIBERNATE_SPATIAL_VERSION.jar "http://nexus.e-is.pro/nexus/content/groups/public/org/hibernate/hibernate-spatial/4.3/hibernate-spatial-4.3.jar"


RUN curl -o $HIBERNATE_MODULE/jts-$JTS_VERSION.jar "http://central.maven.org/maven2/com/vividsolutions/jts/$JTS_VERSION/jts-$JTS_VERSION.jar"

RUN mkdir -p $JBOSS_HOME/modules/org/postgresql/main
COPY postgres/module.xml $POSTGRES_MODULE
RUN curl -o $POSTGRES_MODULE/postgis-jdbc-$POSTGIS_VERSION.jar "https://www.aht-group.com/nexus/content/repositories/public/org/postgis/postgis-jdbc/$POSTGIS_VERSION/postgis-jdbc-$POSTGIS_VERSION.jar"
RUN curl -o $POSTGRES_MODULE/postgresql-$POSTGRES_VERSION.jar "http://central.maven.org/maven2/org/postgresql/postgresql/$POSTGRES_VERSION/postgresql-$POSTGRES_VERSION.jar"
															   																
# Active MQ
RUN curl -o $DEPLOYMENTS/activemq-rar.rar "https://repo1.maven.org/maven2/org/apache/activemq/activemq-rar/$ACTIVEMQ_VERSION/activemq-rar-$ACTIVEMQ_VERSION.rar"

# Standalone config
COPY standalone.conf $JBOSS_HOME/bin/
COPY standalone-uvms.xml $JBOSS_HOME/standalone/configuration/
COPY app-roles.properties $JBOSS_HOME/standalone/configuration/
COPY client.truststore $JBOSS_HOME/standalone/configuration/
COPY server.keystore $JBOSS_HOME/standalone/configuration/
COPY application-roles.properties $JBOSS_HOME/standalone/configuration/
COPY application-users.properties $JBOSS_HOME/standalone/configuration/
COPY mgmt-groups.properties $JBOSS_HOME/standalone/configuration/
COPY mgmt-users.properties $JBOSS_HOME/standalone/configuration/
COPY start.sh /opt/jboss/

# Create log directory
USER root
RUN mkdir -p /app/logs
RUN chown -R jboss:jboss /app/logs
RUN chmod 777 /app/logs
RUN mkdir -p /opt/jboss/wildfly/standalone/log/
RUN chown -R jboss:jboss /opt/jboss/wildfly/standalone/log
RUN chmod 777 /opt/jboss/wildfly/standalone/log/
RUN chmod 755 /opt/jboss/start.sh
USER jboss

# Hibernate Search Index dirs
RUN mkdir -p /opt/jboss/wildfly/standalone/deployments/hibernatesearch/eu.europa.ec.fisheries.uvms.entity.model.AssetHistory

USER root
RUN curl -o /etc/yum.repos.d/epel-apache-maven.repo https://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo
RUN yum install -y apache-maven
RUN yum install -y postgresql
COPY settings.xml /root/.m2/settings.xml
RUN chown -R jboss:jboss /opt/jboss
USER jboss

# Expose stuff to outside world
EXPOSE 8787
VOLUME ["/app/logs", "/opt/jboss/wildfly/standalone/log"]

CMD ["/opt/jboss/start.sh", "/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-uvms.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug"]
