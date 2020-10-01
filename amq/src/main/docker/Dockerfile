FROM rmohr/activemq:5.15.9-alpine

VOLUME ["/opt/$ACTIVEMQ/data"]

USER root
RUN set -x && \
    apk --no-cache add procps && \
    chmod 600 /opt/$ACTIVEMQ/conf/jmx.*
    
USER activemq

COPY activemq.xml users.properties /opt/$ACTIVEMQ/conf/
COPY env /opt/$ACTIVEMQ/bin/
RUN sed -i -e 's/\r//' /opt/$ACTIVEMQ/bin/env

ENV JMX_HOSTNAME=localhost JMX_RMI_PORT=1099

CMD ["/bin/sh", "-c", "bin/activemq console xbean:/opt/$ACTIVEMQ/conf/activemq.xml"]
