FROM jboss/base-jdk:8
ENV ACTIVEMQ_VERSION 5.13.2
ENV ACTIVEMQ_HOME /opt/jboss/activemq

RUN cd $HOME \
    && curl -O https://archive.apache.org/dist/activemq/$ACTIVEMQ_VERSION/apache-activemq-$ACTIVEMQ_VERSION-bin.tar.gz \
    && tar xf apache-activemq-$ACTIVEMQ_VERSION-bin.tar.gz \
    && ls -l


RUN cd $HOME \
    && mv $HOME/apache-activemq-$ACTIVEMQ_VERSION $ACTIVEMQ_HOME \
    && rm apache-activemq-$ACTIVEMQ_VERSION-bin.tar.gz

COPY activemq.xml $ACTIVEMQ_HOME/apache-activemq-$ACTIVEMQ_VERSION/conf
COPY users.properties $ACTIVEMQ_HOME/apache-activemq-$ACTIVEMQ_VERSION/conf
COPY env $ACTIVEMQ_HOME/apache-activemq-$ACTIVEMQ_VERSION/bin

RUN ls -l $ACTIVEMQ_HOME/bin

EXPOSE 61616 8161

VOLUME ["/opt/jboss/activemq"]

CMD ["/opt/jboss/activemq/bin/activemq", "console"]