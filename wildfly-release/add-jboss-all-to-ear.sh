#!bin/bash

addJbossAll() {
    echo "Add jboss-all.xml to $1"
    unzip /opt/jboss/wildfly/standalone/deployments/${1}.ear -d /opt/jboss/wildfly/standalone/deployments/${1}
	cp /opt/jboss/jboss-all.xml /opt/jboss/wildfly/standalone/deployments/${1}/META-INF/
	rm -rf /opt/jboss/wildfly/standalone/deployments/${1}.ear
	jar cvf /opt/jboss/wildfly/standalone/deployments/${1} /opt/jboss/wildfly/standalone/deployments/${1}.ear
	jar -cvf /opt/jboss/wildfly/standalone/deployments/${1}.ear  -C /opt/jboss/wildfly/standalone/deployments/${1} .
	rm -rf /opt/jboss/wildfly/standalone/deployments/${1}
	
}

chmod 755 /opt/jboss/wildfly/standalone/deployments/*.ear
addJbossAll asset-module-3.0.5
addJbossAll audit-dbaccess-module-3.0.4
addJbossAll audit-module-3.0.5
addJbossAll config-dbaccess-module-3.0.4
addJbossAll config-module-3.0.5
addJbossAll deploy-0.5.2
addJbossAll exchange-dbaccess-module-3.0.4
addJbossAll exchange-module-3.0.5
addJbossAll mobileterminal-dbaccess-module-3.0.4
addJbossAll mobileterminal-module-3.0.5
addJbossAll movement-dbaccess-module-postgres-3.0.4
addJbossAll movement-module-3.0.5
addJbossAll rules-dbaccess-module-3.0.3
addJbossAll rules-module-3.0.1
addJbossAll spatial-module-postgres-0.5.6-SNAPSHOT
