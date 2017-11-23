FROM njmittet/alpine-openjdk:jdk8

MAINTAINER Nils Jørgen Mittet <njmittet@gmail.com>

ENV WILDFLY_VERSION 11.0.0.Final
ENV JBOSS_HOME /opt/jboss/wildfly
ENV STANDALONE standalone

USER root
RUN mkdir -p /opt/jboss && adduser -D -h /opt/jboss jboss && apk update && apk add curl && rm -rf /var/cache/apk/*

USER jboss
WORKDIR /opt/jboss
RUN curl -O http://download.jboss.org/wildfly/$WILDFLY_VERSION/wildfly-$WILDFLY_VERSION.zip && unzip wildfly-$WILDFLY_VERSION.zip && mv $HOME/wildfly-$WILDFLY_VERSION $HOME/wildfly && rm wildfly-$WILDFLY_VERSION.zip

EXPOSE 8080 9990
CMD /opt/jboss/wildfly/bin/standalone.sh --server-config=$STANDALONE.xml -b 0.0.0.0

ENV TZ=CET
    
COPY dependencies/activemq-rar-${wildfly.activemq.rar.version}.rar $DEPLOYMENTS/activemq-rar.rar

COPY postgres/module.xml $POSTGRES_MODULE
COPY dependencies/postgresql-${wildfly.postgres.module.postgres.version}.jar $POSTGRES_MODULE 
COPY dependencies/postgis-jdbc-${wildfly.postgres.module.postgis.version}.jar $POSTGRES_MODULE

# Create log directory
# note that we should use git update-index --chmod=+x
# instead of doing this in the docker build
USER root
RUN mkdir -p /app/logs && \
    chown -R jboss:jboss /app/logs && \
    chmod 777 /app/logs && \
    mkdir -p /opt/jboss/wildfly/standalone/log/ && \
    chown -R jboss:jboss /opt/jboss/wildfly/standalone/log && \
    chmod 777 /opt/jboss/wildfly/standalone/log/ && \
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

# Expose stuff to outside world
EXPOSE 8787
VOLUME ["/app/logs", "/opt/jboss/wildfly/standalone/log"]