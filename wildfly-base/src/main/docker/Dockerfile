FROM njmittet/alpine-wildfly:8.2.1.Final

ENV WILDFLY_VERSION=8.2.1.Final HIBERNATE_VERSION=4.3.11.Final HIBERNATE_SPATIAL_VERSION=4.3 JTS_VERSION=1.13 POSTGIS_VERSION=1.5.3 POSTGRES_VERSION=42.1.4.jre7 ACTIVEMQ_VERSION=5.14.5 TZ=CET

ENV NEXUS http://nexus.focus.fish/nexus/service/local/artifact/maven/content
ENV HIBERNATE_MODULE $JBOSS_HOME/modules/system/layers/base/org/hibernate/main
ENV POSTGRES_MODULE $JBOSS_HOME/modules/org/postgresql/main
ENV DEPLOYMENTS $JBOSS_HOME/standalone/deployments
ENV PGPASSWORD=postgres

# PostGis


RUN curl -o $HIBERNATE_MODULE/hibernate-core-$HIBERNATE_VERSION.jar "http://central.maven.org/maven2/org/hibernate/hibernate-core/$HIBERNATE_VERSION/hibernate-core-$HIBERNATE_VERSION.jar" && \
    curl -o $HIBERNATE_MODULE/hibernate-envers-$HIBERNATE_VERSION.jar "http://central.maven.org/maven2/org/hibernate/hibernate-envers/$HIBERNATE_VERSION/hibernate-envers-$HIBERNATE_VERSION.jar" && \
    curl -o $HIBERNATE_MODULE/hibernate-entitymanager-$HIBERNATE_VERSION.jar "http://central.maven.org/maven2/org/hibernate/hibernate-entitymanager/$HIBERNATE_VERSION/hibernate-entitymanager-$HIBERNATE_VERSION.jar" && \
    curl -o $HIBERNATE_MODULE/hibernate-infinispan-$HIBERNATE_VERSION.jar "http://central.maven.org/maven2/org/hibernate/hibernate-infinispan/$HIBERNATE_VERSION/hibernate-infinispan-$HIBERNATE_VERSION.jar" && \
    curl -o $HIBERNATE_MODULE/hibernate-spatial-$HIBERNATE_SPATIAL_VERSION.jar "http://nexus.e-is.pro/nexus/content/groups/public/org/hibernate/hibernate-spatial/4.3/hibernate-spatial-4.3.jar" && \
    curl -o $HIBERNATE_MODULE/jts-$JTS_VERSION.jar "http://central.maven.org/maven2/com/vividsolutions/jts/$JTS_VERSION/jts-$JTS_VERSION.jar" && \
    mkdir -p $JBOSS_HOME/modules/org/postgresql/main && \
    curl -o $POSTGRES_MODULE/postgis-jdbc-$POSTGIS_VERSION.jar "https://adams.cms.waikato.ac.nz/nexus/content/repositories/adams-thirdparty/org/postgis/postgis-jdbc/$POSTGIS_VERSION/postgis-jdbc-$POSTGIS_VERSION.jar" && \
    curl -o $POSTGRES_MODULE/postgresql-$POSTGRES_VERSION.jar "http://central.maven.org/maven2/org/postgresql/postgresql/$POSTGRES_VERSION/postgresql-$POSTGRES_VERSION.jar" && \
    curl -o $DEPLOYMENTS/activemq-rar.rar "https://repo1.maven.org/maven2/org/apache/activemq/activemq-rar/$ACTIVEMQ_VERSION/activemq-rar-$ACTIVEMQ_VERSION.rar" && \
    mkdir -p /opt/jboss/wildfly/standalone/deployments/hibernatesearch/eu.europa.ec.fisheries.uvms.entity.model.AssetHistory

COPY postgres/module.xml $POSTGRES_MODULE
COPY hibernate/module.xml $HIBERNATE_MODULE

# Standalone config
COPY standalone.conf $JBOSS_HOME/bin/
COPY standalone-uvms.xml app-roles.properties client.truststore server.keystore application-roles.properties application-users.properties mgmt-groups.properties mgmt-users.properties $JBOSS_HOME/standalone/configuration/
COPY start.sh /opt/jboss/
COPY fakesmtp /opt/jboss/fakesmtp

# Create log directory
# note that we should use git update-index --chmod=+x
# instead of doing this in the docker build
USER root
RUN mkdir -p /opt/jboss/fakesmtp/logs && \
    chown -R jboss:jboss /opt/jboss/fakesmtp && \
    chmod +x /opt/jboss/fakesmtp/fakesmtp.sh && \
    sed -i '2 a /opt/jboss/fakesmtp/fakesmtp.sh start' /opt/jboss/wildfly/bin/standalone.sh && \
    mkdir -p /app/logs && \
    chown -R jboss:jboss /app/logs && \
    chmod 777 /app/logs && \
    mkdir -p /opt/jboss/wildfly/standalone/log/ && \
    chown -R jboss:jboss /opt/jboss/wildfly/standalone/log && \
    chmod 777 /opt/jboss/wildfly/standalone/log/ && \
    chmod 755 /opt/jboss/start.sh && \
    chown -R jboss:jboss /opt/jboss
# install TZDATA, set timesone then delete TZDATA
# alpine linux does not include curl. install it, but delete the apk cache to keep layer size small
RUN echo "@edge http://nl.alpinelinux.org/alpine/edge/main" >> /etc/apk/repositories && \
    apk update && apk add tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    apk del tzdata && \
    rm -rf /var/cache/apk/*

USER jboss

# Expose stuff to outside world
EXPOSE 8787
VOLUME ["/app/logs", "/opt/jboss/wildfly/standalone/log"]

# we don't have to wait for usm.databasechangeloglock locked on the db.
# the wait should be part of the docker compose file on the postgres side
# or on the maven docker plugin config
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-uvms.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug"]
