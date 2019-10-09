FROM adoptopenjdk/openjdk8-openj9:jdk8u181-b13_openj9-0.9.0-alpine-slim

MAINTAINER Nils Jørgen Mittet <njmittet@gmail.com>

ENV WILDFLY_VERSION 18.0.0.Final
ENV JBOSS_HOME /opt/jboss/wildfly

USER root
RUN mkdir -p /opt/jboss && adduser -D -h /opt/jboss jboss && apk update && apk add curl && apk add libaio && rm -rf /var/cache/apk/*

USER jboss
WORKDIR /opt/jboss
RUN curl -LO http://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.zip && unzip wildfly-$WILDFLY_VERSION.zip && mv $HOME/wildfly-$WILDFLY_VERSION $HOME/wildfly && rm wildfly-$WILDFLY_VERSION.zip

EXPOSE 8080 9990 5445 5455

CMD ["/opt/jboss/start.sh", "/opt/jboss/wildfly/bin/standalone.sh", "-c", "standalone-uvms.xml", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0", "--debug"]

ENV TZ=CET


ENV HIBERNATE_MODULE $JBOSS_HOME/modules/system/layers/base/org/hibernate/main

ENV POSTGRES_MODULE $JBOSS_HOME/modules/org/postgresql/main
ENV DEPLOYMENTS $JBOSS_HOME/standalone/deployments
ENV PGPASSWORD=postgres

RUN mkdir -p $JBOSS_HOME/modules/org/postgresql/main && \
    mkdir -p /opt/jboss/wildfly/standalone/deployments/hibernatesearch/eu.europa.ec.fisheries.uvms.entity.model.AssetHistory


COPY postgres/module.xml $POSTGRES_MODULE
COPY dependencies/postgresql-${wildfly.postgres.module.postgres.version}.jar $POSTGRES_MODULE
COPY dependencies/postgis-jdbc-${wildfly.postgres.module.postgis.version}.jar $POSTGRES_MODULE

COPY hibernate/module.xml $HIBERNATE_MODULE
COPY dependencies/hibernate-core-${wildfly.hibernate.module.spatial.version}.jar $HIBERNATE_MODULE
COPY dependencies/hibernate-envers-${wildfly.hibernate.module.spatial.version}.jar $HIBERNATE_MODULE
COPY dependencies/hibernate-spatial-${wildfly.hibernate.module.spatial.version}.jar $HIBERNATE_MODULE
COPY dependencies/jts-core-${wildfly.hibernate.module.jts.version}.jar $HIBERNATE_MODULE
COPY dependencies/geolatte-geom-${wildfly.hibernate.module.geolatte.version}.jar $HIBERNATE_MODULE

# Standalone config
COPY standalone.conf $JBOSS_HOME/bin/
COPY standalone-uvms.xml $JBOSS_HOME/standalone/configuration/
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

# NPE in sun.awt.FontConfiguration.getVersion(...), see https://github.com/AdoptOpenJDK/openjdk-docker/issues/75
RUN apk add --no-cache fontconfig
RUN ln -s /usr/lib/libfontconfig.so.1 /usr/lib/libfontconfig.so && \
    ln -s /lib/libuuid.so.1 /usr/lib/libuuid.so.1 && \
    ln -s /lib/libc.musl-x86_64.so.1 /usr/lib/libc.musl-x86_64.so.1
ENV LD_LIBRARY_PATH /usr/lib

USER jboss

RUN $JBOSS_HOME/bin/add-user.sh -u 'admin' -p 'Wildfly4ever!'
RUN $JBOSS_HOME/bin/add-user.sh -a 'test' -p 'test' -g guest

# Expose stuff to outside world
EXPOSE 8787
VOLUME ["/app/logs", "/opt/jboss/wildfly/standalone/log"]
