FROM njmittet/alpine-wildfly:8.2.1.Final

ENV WILDFLY_VERSION=8.2.1.Final TZ=CET

ENV NEXUS http://nexus.focus.fish/nexus/service/local/artifact/maven/content
ENV HIBERNATE_MODULE $JBOSS_HOME/modules/system/layers/base/org/hibernate/main
ENV POSTGRES_MODULE $JBOSS_HOME/modules/org/postgresql/main
ENV DEPLOYMENTS $JBOSS_HOME/standalone/deployments
ENV PGPASSWORD=postgres

RUN mkdir -p $JBOSS_HOME/modules/org/postgresql/main && \
    mkdir -p /opt/jboss/wildfly/standalone/deployments/hibernatesearch/eu.europa.ec.fisheries.uvms.entity.model.AssetHistory
    
COPY dependencies/activemq-rar-${wildfly.activemq.rar.version}.rar $DEPLOYMENTS/activemq-rar.rar

COPY postgres/module.xml $POSTGRES_MODULE
COPY dependencies/postgresql-${wildfly.postgres.module.postgres.version}.jar $POSTGRES_MODULE 
COPY dependencies/postgis-jdbc-${wildfly.postgres.module.postgis.version}.jar $POSTGRES_MODULE

COPY hibernate/module.xml $HIBERNATE_MODULE
COPY dependencies/hibernate-spatial-${wildfly.hibernate.module.spatial.version}.jar $HIBERNATE_MODULE
COPY dependencies/hibernate-core-${wildfly.hibernate.module.hibernate.version}.jar $HIBERNATE_MODULE
COPY dependencies/hibernate-entitymanager-${wildfly.hibernate.module.hibernate.version}.jar $HIBERNATE_MODULE
COPY dependencies/hibernate-envers-${wildfly.hibernate.module.hibernate.version}.jar $HIBERNATE_MODULE
COPY dependencies/hibernate-infinispan-${wildfly.hibernate.module.hibernate.version}.jar $HIBERNATE_MODULE
COPY dependencies/jts-${wildfly.hibernate.module.jts.version}.jar $HIBERNATE_MODULE

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
    apk update && apk add tzdata ttf-dejavu postgresql-client bash && \
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
CMD ["/opt/jboss/start.sh", "/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-uvms.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug"]
