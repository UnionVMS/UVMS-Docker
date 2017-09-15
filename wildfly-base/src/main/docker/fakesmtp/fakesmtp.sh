#!/bin/sh
start() {
  # we need to use a port higher than 1024 otherwise we get a bind error. lower ports are privileged
  java -jar /opt/jboss/fakesmtp/fakeSMTP-2.0.jar -s -b -p 1025 \
    -o /opt/jboss/fakesmtp/out/ -a 127.0.0.1 \
    >> /opt/jboss/fakesmtp/logs/fakesmtp.log &
    # if we wanted daily logs:
    # >> /opt/fakesmtp/logs/fakesmtp-'date +%Y%m%d'.log &
}

stop() {
  kill `ps -a > /tmp/ps.out && grep -i fakesmtp /tmp/ps.out | \
    awk '{print $1}'`
}

case $1 in
  start|stop) "$1" ;;
esac