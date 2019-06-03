FROM fabric8/java-alpine-openjdk8-jdk:1.6.3

ENV ACTIVEMQ_VERSION 5.15.9
ENV ACTIVEMQ apache-activemq-$ACTIVEMQ_VERSION
ENV ACTIVEMQ_TCP=61616 ACTIVEMQ_AMQP=5672 ACTIVEMQ_STOMP=61613 ACTIVEMQ_MQTT=1883 ACTIVEMQ_WS=61614 ACTIVEMQ_UI=8161 HAWTIO=8080

ENV ACTIVEMQ_HOME /opt/activemq
ENV HAWTIO_HOME /opt/hawtio

RUN set -x && \
    mkdir -p /opt && \
    apk --update add --virtual build-dependencies curl && apk add ttf-dejavu && \
    curl -s -S https://archive.apache.org/dist/activemq/$ACTIVEMQ_VERSION/$ACTIVEMQ-bin.tar.gz | tar xvz -C /opt && \
    ln -s /opt/$ACTIVEMQ $ACTIVEMQ_HOME && \
    addgroup -S activemq && adduser -S -H -G activemq -h $ACTIVEMQ_HOME activemq && \
    chown -R activemq:activemq /opt/$ACTIVEMQ && \
    chown -h activemq:activemq $ACTIVEMQ_HOME

RUN set -x && \
    mkdir -p $HAWTIO_HOME

RUN set -x && \
    curl -s -S https://oss.sonatype.org/content/repositories/public/io/hawt/hawtio-app/2.1.0/hawtio-app-2.1.0.jar > $HAWTIO_HOME/hawtio-app-2.1.0.jar && \
    chown activemq:activemq $HAWTIO_HOME/hawtio-app-2.1.0.jar && \
    apk del build-dependencies && \
    rm -rf /var/cache/apk/*

USER activemq

WORKDIR $ACTIVEMQ_HOME
EXPOSE $ACTIVEMQ_TCP $ACTIVEMQ_AMQP $ACTIVEMQ_STOMP $ACTIVEMQ_MQTT $ACTIVEMQ_WS $ACTIVEMQ_UI $HAWTIO

CMD ["/bin/sh", "-c", "bin/activemq start && java -jar $HAWTIO_HOME/hawtio-app-2.1.0.jar"]

COPY activemq.xml users.properties env /opt/$ACTIVEMQ/conf/

VOLUME ["/opt/$ACTIVEMQ/data"]