#!/bin/bash

if [ "x${JBOSS_HOME}" = "x" ]; then
  JBOSS_HOME=/opt/wildfly/current
fi
SERVER_CLI=$JBOSS_HOME/bin/jboss-cli.sh

DIR_WHERE_THIS_SCRIPT_IS="$(cd "$(dirname "$0")" && pwd)"
(
  cd $DIR_WHERE_THIS_SCRIPT_IS

  echo "Creating EFR JMS queues"
  $SERVER_CLI \
    --file=efr_messaging_local_docker_only.cli \
    -Djboss.server.log.dir=$JBOSS_HOME/standalone/tmp \
	    | grep -v '{"outcome" => "success"}'

)
