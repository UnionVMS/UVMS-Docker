#!/bin/bash

if [ "x${JBOSS_HOME}" = "x" ]; then
  JBOSS_HOME=/opt/wildfly/current
fi
SERVER_CLI=$JBOSS_HOME/bin/jboss-cli.sh

DIR_WHERE_THIS_SCRIPT_IS="$(cd "$(dirname "$0")" && pwd)"
(
  cd $DIR_WHERE_THIS_SCRIPT_IS

  if [ "$1" != "" ]; then

    echo "Configuring Wildfly for EFR"
    echo
    echo "System properties, logs, and other..."
    $SERVER_CLI \
      --file=efr_configuration.cli \
      --properties=$1 \
      -Djboss.server.log.dir=$JBOSS_HOME/standalone/tmp \
	    | grep -v '{"outcome" => "success"}'
    echo

    echo "Messaging..."
    $SERVER_CLI \
      --file=efr_messaging.cli \
      --properties=$1 \
      -Djboss.server.log.dir=$JBOSS_HOME/standalone/tmp \
	    | grep -v '{"outcome" => "success"}'
    echo

    echo "** Note: EFR datasources were not created by this script. On standard Hav Wildfly machines scripts for that are in /opt/wildfly/hav_git/wildfly"
    echo

  else
    echo
    echo "Usage: $0 <properties file>  (will look for properties file in same dir as the script is located)"
    echo "Example: $0 efr_env-test.properties"
  fi
)
