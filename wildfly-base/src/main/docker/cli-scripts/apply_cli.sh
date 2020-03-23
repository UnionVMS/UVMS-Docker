#!/bin/bash

echo "Configuring Wildfly for UVMS"
$JBOSS_HOME/bin/jboss-cli.sh \
	--file=$JBOSS_HOME/cli-scripts/uvms_configuration.cli \
	--properties=$JBOSS_HOME/cli-scripts/uvms.properties \
	-Djboss.server.log.dir=$JBOSS_HOME/standalone/tmp \
	| grep -v '{"outcome" => "success"}'

echo "Creating UVMS datasources"
$JBOSS_HOME/bin/jboss-cli.sh \
	--file=$JBOSS_HOME/cli-scripts/uvms_datasources.cli \
	--properties=$JBOSS_HOME/cli-scripts/uvms.properties \
	-Djboss.server.log.dir=$JBOSS_HOME/standalone/tmp \
	| grep -v '{"outcome" => "success"}'

echo "Configuring UVMS messaging"
$JBOSS_HOME/bin/jboss-cli.sh \
	--file=$JBOSS_HOME/cli-scripts/uvms_messaging.cli \
	-Djboss.server.log.dir=$JBOSS_HOME/standalone/tmp \
	| grep -v '{"outcome" => "success"}'