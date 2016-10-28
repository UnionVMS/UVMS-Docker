FROM jboss/wildfly:8.2.0.Final

ENV WILDFLY_VERSION 8.2.0.Final
ENV HIBERNATE_SPATIAL_VERSION 4.3
ENV JTS_VERSION 1.13
ENV POSTGIS_VERSION 1.5.3
ENV POSTGRES_VERSION 9.3-1102-jdbc4
ENV ACTIVEMQ_VERSION 5.13.2

ENV NEXUS http://46.21.110.250:8081/nexus/service/local/artifact/maven/content
ENV HIBERNATE_MODULE $JBOSS_HOME/modules/system/layers/base/org/hibernate/main
ENV POSTGRES_MODULE $JBOSS_HOME/modules/org/postgresql/main
ENV DEPLOYMENTS $JBOSS_HOME/standalone/deployments

# Standalone config
COPY standalone-uvms.xml $JBOSS_HOME/standalone/configuration/
COPY app-roles.properties $JBOSS_HOME/standalone/configuration/
COPY client.truststore $JBOSS_HOME/standalone/configuration/
COPY server.keystore $JBOSS_HOME/standalone/configuration/

# PostGis
COPY hibernate/module.xml $HIBERNATE_MODULE
RUN curl -o $HIBERNATE_MODULE/hibernate-spatial-$HIBERNATE_SPATIAL_VERSION.jar "http://www.hibernatespatial.org/repository/org/hibernate/hibernate-spatial/$HIBERNATE_SPATIAL_VERSION/hibernate-spatial-$HIBERNATE_SPATIAL_VERSION.jar"
RUN curl -o $HIBERNATE_MODULE/jts-$JTS_VERSION.jar "http://central.maven.org/maven2/com/vividsolutions/jts/$JTS_VERSION/jts-$JTS_VERSION.jar"

RUN mkdir -p $JBOSS_HOME/modules/org/postgresql/main
COPY postgres/module.xml $POSTGRES_MODULE
RUN curl -o $POSTGRES_MODULE/postgis-jdbc-$POSTGIS_VERSION.jar "$NEXUS?g=org.postgis&a=postgis-jdbc&v=$POSTGIS_VERSION&r=public&e=jar"
RUN curl -o $POSTGRES_MODULE/postgresql-$POSTGRES_VERSION.jar "http://central.maven.org/maven2/org/postgresql/postgresql/$POSTGRES_VERSION/postgresql-$POSTGRES_VERSION.jar"

# Active MQ
RUN curl -o $DEPLOYMENTS/activemq-rar.rar "$NEXUS?g=org.apache.activemq&a=activemq-rar&v=$ACTIVEMQ_VERSION&r=public&e=rar"

# DB Module
RUN curl -o $DEPLOYMENTS/config-dbaccess-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.config&a=config-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/asset-dbaccess-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.asset&a=asset-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/audit-dbaccess-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.audit&a=audit-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/exchange-dbaccess-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.exchange&a=exchange-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/mobileterminal-dbaccess-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.mobileterminal&a=mobileterminal-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/movement-dbaccess-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.movement&a=movement-dbaccess-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/rules-dbaccess-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.rules&a=rules-dbaccess-module&v=LATEST&r=public&e=ear"

# APP Module
RUN curl -o $DEPLOYMENTS/config-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.config&a=config-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/asset-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.asset&a=asset-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/audit-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.audit&a=audit-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/exchange-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.exchange&a=exchange-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/mobileterminal-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.mobileterminal&a=mobileterminal-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/movement-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.movement&a=movement-module&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/rules-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.rules&a=rules-module&v=LATEST&r=public&e=ear"

RUN curl -o $DEPLOYMENTS/spatial-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.spatial&a=deploy-spatial-postgres&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/reporting-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.reporting&a=deploy&v=LATEST&r=public&e=ear"
RUN curl -o $DEPLOYMENTS/user-module.ear "$NEXUS?g=eu.europa.ec.fisheries.uvms.user&a=user-module&v=LATEST&r=public&e=ear"

# Frontend
RUN curl -o $DEPLOYMENTS/unionvms-web.war "$NEXUS?g=eu.europa.ec.fisheries.uvms&a=unionvms-web&v=LATEST&r=public&e=war"

#RUN ls -l $JBOSS_HOME/standalone/deployments
#RUN ls -l $JBOSS_HOME/standalone/configuration
#RUN ls -l $HIBERNATE_MODULE
#RUN ls -l $POSTGRES_MODULE

# Create log directory
USER root
RUN mkdir -p /app/logs
run chown jboss /app/logs
USER jboss

# Expose stuff to outside world
EXPOSE 8787
VOLUME ["/app/logs"]
VOLUME ["/opt/jboss/wildfly/standalone/logs"]

# Create admin user
RUN /opt/jboss/wildfly/bin/add-user.sh admin wildfly --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-uvms.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug"]