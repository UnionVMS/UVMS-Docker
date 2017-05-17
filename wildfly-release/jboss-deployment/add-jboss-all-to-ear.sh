#!bin/bash

addJbossAll() {
    echo "Add jboss-all.xml to $1"
    unzip /opt/jboss/wildfly/standalone/deployments/${1}.ear -d /opt/jboss/wildfly/standalone/deployments/${1}
	cp $2 /opt/jboss/wildfly/standalone/deployments/${1}/META-INF/
	rm -rf /opt/jboss/wildfly/standalone/deployments/${1}.ear
	jar cvf /opt/jboss/wildfly/standalone/deployments/${1} /opt/jboss/wildfly/standalone/deployments/${1}.ear
	jar -cvf /opt/jboss/wildfly/standalone/deployments/${1}.ear  -C /opt/jboss/wildfly/standalone/deployments/${1} .
	rm -rf /opt/jboss/wildfly/standalone/deployments/${1}
	
}

chmod 755 /opt/jboss/wildfly/standalone/deployments/*.ear
addJbossAll user-module-1.6.1 /opt/jboss/jboss-deployment/user/jboss-all.xml 
addJbossAll config-dbaccess-module-3.0.4 /opt/jboss/jboss-deployment/config-db/jboss-all.xml 
addJbossAll config-module-3.0.5 /opt/jboss/jboss-deployment/config/jboss-all.xml
addJbossAll spatial-module-postgres-0.5.6-SNAPSHOT /opt/jboss/jboss-deployment/spatial/jboss-all.xml
addJbossAll deploy-0.5.2 /opt/jboss/jboss-deployment/reporting/jboss-all.xml
addJbossAll movement-module-3.0.7 /opt/jboss/jboss-deployment/movement/jboss-all.xml
addJbossAll asset-module-3.0.5 /opt/jboss/jboss-deployment/asset/jboss-all.xml
addJbossAll audit-dbaccess-module-3.0.4 /opt/jboss/jboss-deployment/general/jboss-all.xml
addJbossAll audit-module-3.0.5 /opt/jboss/jboss-deployment/audit/jboss-all.xml
addJbossAll exchange-dbaccess-module-3.0.4 /opt/jboss/jboss-deployment/general/jboss-all.xml
addJbossAll exchange-module-3.0.5 /opt/jboss/jboss-deployment/exchange/jboss-all.xml
addJbossAll mobileterminal-module-3.0.7 /opt/jboss/jboss-deployment/mobile/jboss-all.xml
addJbossAll rules-dbaccess-module-3.0.3 /opt/jboss/jboss-deployment/general/jboss-all.xml
addJbossAll rules-module-3.0.1 /opt/jboss/jboss-deployment/rules/jboss-all.xml
