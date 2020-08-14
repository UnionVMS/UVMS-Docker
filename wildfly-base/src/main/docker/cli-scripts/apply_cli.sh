#!/bin/bash

while [ "$#" -gt 0 ]
do
    if [ $1 = "-c" ]; then
        shift
        SERVER_CONFIG="$1"
    fi
    shift
done
sed -i s/standalone-full.xml/$SERVER_CONFIG/g $JBOSS_HOME/cli-scripts/uvms.properties

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
	--properties=$JBOSS_HOME/cli-scripts/uvms.properties \
	-Djboss.server.log.dir=$JBOSS_HOME/standalone/tmp \
	| grep -v '{"outcome" => "success"}'

if [ $SERVER_CONFIG = "standalone-full-ha.xml" ]; then
	echo "Configuring HA"
	$JBOSS_HOME/bin/jboss-cli.sh \
		--file=$JBOSS_HOME/cli-scripts/uvms_ha.cli \
		--properties=$JBOSS_HOME/cli-scripts/uvms.properties \
		-Djboss.server.log.dir=$JBOSS_HOME/standalone/tmp \
		| grep -v '{"outcome" => "success"}'
	sed -i -r s/-Xmx[0-9]\{4\}m/-Xmx2048m/g $JBOSS_HOME/bin/standalone.conf
fi