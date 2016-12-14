FROM jboss/wildfly:8.2.0.Final

ENV WILDFLY_VERSION 8.2.0.Final
ENV HIBERNATE_SPATIAL_VERSION 4.3
ENV JTS_VERSION 1.13
ENV POSTGIS_VERSION 1.5.3
ENV POSTGRES_VERSION 9.3-1102-jdbc4
ENV ACTIVEMQ_VERSION 5.13.2

ENV NEXUS http://nexus.focus.fish/nexus/service/local/artifact/maven/content
ENV HIBERNATE_MODULE $JBOSS_HOME/modules/system/layers/base/org/hibernate/main
ENV POSTGRES_MODULE $JBOSS_HOME/modules/org/postgresql/main
ENV DEPLOYMENTS $JBOSS_HOME/standalone/deployments
ENV AUTO_DEPLOYMENTS $JBOSS_HOME/standalone/auto

# PostGis
COPY hibernate/module.xml $HIBERNATE_MODULE
RUN curl -o $HIBERNATE_MODULE/hibernate-spatial-$HIBERNATE_SPATIAL_VERSION.jar "http://www.hibernatespatial.org/repository/org/hibernate/hibernate-spatial/$HIBERNATE_SPATIAL_VERSION/hibernate-spatial-$HIBERNATE_SPATIAL_VERSION.jar"
RUN curl -o $HIBERNATE_MODULE/jts-$JTS_VERSION.jar "http://central.maven.org/maven2/com/vividsolutions/jts/$JTS_VERSION/jts-$JTS_VERSION.jar"

RUN mkdir -p $JBOSS_HOME/modules/org/postgresql/main
COPY postgres/module.xml $POSTGRES_MODULE
RUN curl -o $POSTGRES_MODULE/postgis-jdbc-$POSTGIS_VERSION.jar "$NEXUS?g=org.postgis&a=postgis-jdbc&v=$POSTGIS_VERSION&r=public&e=jar"
RUN curl -o $POSTGRES_MODULE/postgresql-$POSTGRES_VERSION.jar "http://central.maven.org/maven2/org/postgresql/postgresql/$POSTGRES_VERSION/postgresql-$POSTGRES_VERSION.jar"

# Active MQ
RUN mkdir -p /opt/jboss/wildfly/standalone/auto
RUN curl -o $AUTO_DEPLOYMENTS/activemq-rar.rar "$NEXUS?g=org.apache.activemq&a=activemq-rar&v=$ACTIVEMQ_VERSION&r=public&e=rar"

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

# Create log directory
USER root
RUN mkdir -p /app/logs
RUN chown jboss /app/logs
USER jboss
RUN mkdir -p /app/logs/twostage/poll

# Hibernate Search Index dirs
RUN mkdir -p /opt/jboss/wildfly/standalone/deployments/hibernatesearch/eu.europa.ec.fisheries.uvms.entity.model.AssetHistory


# Expose stuff to outside world
EXPOSE 8787
VOLUME ["/app/logs", "/opt/jboss/wildfly/standalone/log"]

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-uvms.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug"]