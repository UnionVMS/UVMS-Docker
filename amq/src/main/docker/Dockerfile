FROM rmohr/activemq:5.15.4-alpine

COPY activemq.xml users.properties env /opt/$ACTIVEMQ/conf/

VOLUME ["/opt/$ACTIVEMQ/data"]