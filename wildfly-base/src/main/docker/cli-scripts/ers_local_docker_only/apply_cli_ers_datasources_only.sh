#!/bin/bash

if [ "x${JBOSS_HOME}" = "x" ]; then
  JBOSS_HOME=/opt/wildfly/current
fi
SERVER_CLI=$JBOSS_HOME/bin/jboss-cli.sh

DIR_WHERE_THIS_SCRIPT_IS="$(cd "$(dirname "$0")" && pwd)"
(
  cd $DIR_WHERE_THIS_SCRIPT_IS

  if [ "$1" != "" ]; then

    echo "Creating ERS datasources"
    $SERVER_CLI \
      --file=ers_datasources.cli \
      --properties=$1 \
      -Djboss.server.log.dir=$JBOSS_HOME/standalone/tmp \
	    | grep -v '{"outcome" => "success"}'

  else
    echo
    echo "Usage: $0 <properties file>  (will look for properties file in same dir as the script is located)"
    echo "Example: $0 ers.properties"
  fi
)
