FROM fabric8/java-alpine-openjdk8-jdk:1.8.0

MAINTAINER Nils Jørgen Mittet <njmittet@gmail.com>

ENV WILDFLY_VERSION 20.0.1.Final
ENV JBOSS_HOME /opt/jboss/wildfly

USER root
RUN mkdir -p /opt/jboss && adduser -D -h /opt/jboss jboss && apk update && apk add curl && rm -rf /var/cache/apk/*

ADD wildfly-$WILDFLY_VERSION.tar.gz /opt/jboss











