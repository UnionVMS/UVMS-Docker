FROM fabric8/java-alpine-openjdk8-jdk:1.8.0

MAINTAINER Nils Jørgen Mittet <njmittet@gmail.com>

ENV WILDFLY_VERSION 20.0.1.Final
ENV JBOSS_HOME /opt/jboss/wildfly

USER root
RUN mkdir -p /opt/jboss && adduser -D -h /opt/jboss jboss && apk update && apk add curl && rm -rf /var/cache/apk/*

ADD wildfly-$WILDFLY_VERSION.tar.gz /opt/jboss
RUN mv /opt/jboss/wildfly-$WILDFLY_VERSION /opt/jboss/wildfly && chown -R jboss /opt/jboss/wildfly
USER jboss
WORKDIR /opt/jboss

EXPOSE 8080 9990

CMD ["/opt/jboss/start.sh", "/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-uvms.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug"]

ENV TZ=CET
ENV HIBERNATE_MODULE $JBOSS_HOME/modules/system/layers/base/org/hibernate/main
ENV POSTGRES_MODULE $JBOSS_HOME/modules/org/postgresql/main
ENV DEPLOYMENTS $JBOSS_HOME/standalone/deployments
ENV PGPASSWORD=postgres
ENV YASSON_MODULE $JBOSS_HOME/modules/system/layers/base/org/eclipse/yasson/main

RUN mkdir -p $JBOSS_HOME/modules/org/postgresql/main && \
    mkdir -p /opt/jboss/wildfly/standalone/deployments/hibernatesearch/eu.europa.ec.fisheries.uvms.entity.model.AssetHistory
COPY dependencies/activemq-rar-${wildfly.activemq.rar.version}.rar $DEPLOYMENTS/activemq-rar.rar
COPY postgres/module.xml $POSTGRES_MODULE
COPY dependencies/postgresql-${wildfly.postgres.module.postgres.version}.jar $POSTGRES_MODULE 
COPY dependencies/postgis-jdbc-${wildfly.postgres.module.postgis.version}.jar $POSTGRES_MODULE
COPY hibernate/module.xml $HIBERNATE_MODULE
COPY dependencies/hibernate-spatial-${wildfly.hibernate.module.spatial.version}.jar $HIBERNATE_MODULE
COPY dependencies/jts-${wildfly.hibernate.module.jts.version}.jar $HIBERNATE_MODULE
COPY dependencies/geolatte-geom-${wildfly.hibernate.module.geolatte.version}.jar $HIBERNATE_MODULE
COPY dependencies/hibernate-spatial-4x-to-5x-wrapper-${wildfly.hibernate.spatial.wrapper.version}.jar $HIBERNATE_MODULE
COPY yasson/module.xml $YASSON_MODULE
COPY dependencies/yasson-1.0.2.jar $YASSON_MODULE

# Standalone config
COPY standalone.conf $JBOSS_HOME/bin/
COPY standalone-uvms.xml $JBOSS_HOME/standalone/configuration/
COPY start.sh /opt/jboss/
COPY fakesmtp /opt/jboss/fakesmtp
COPY aliases.sh /opt/jboss


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
    chmod 777 /opt/jboss/aliases.sh && \
    chown -R jboss:jboss /opt/jboss
# install TZDATA, set timesone then delete TZDATA
# alpine linux does not include curl. install it, but delete the apk cache to keep layer size small
RUN echo "@edge http://nl.alpinelinux.org/alpine/edge/main" >> /etc/apk/repositories && \
    apk update && apk add tzdata ttf-dejavu postgresql-client bash && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    apk del tzdata && \
    rm -rf /var/cache/apk/*

USER jboss

RUN $JBOSS_HOME/bin/add-user.sh -u 'admin' -p 'Wildfly4ever!'

## With this command you'll have 'comodity' aliases like 'serverLogs' or 'activityLogs' or 'editStandalone' or 'configurationFolder' ecc directly after accessing the runing container...
#RUN /bin/bash -c "source /opt/jboss/aliases.sh"
#RUN . /opt/jboss/aliases.sh
#SHELL ["/bin/bash", "-c", "source /opt/jboss/aliases.sh"]

# Expose stuff to outside world
EXPOSE 8787
VOLUME ["/app/logs", "/opt/jboss/wildfly/standalone/log"]










