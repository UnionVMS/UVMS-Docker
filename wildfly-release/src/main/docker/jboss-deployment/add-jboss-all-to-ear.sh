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

addJbossAlltoWar() {
    echo "Add jboss-all.xml to $1"
    unzip /opt/jboss/wildfly/standalone/deployments/${1}.war -d /opt/jboss/wildfly/standalone/deployments/${1}
	#cp $2 /opt/jboss/wildfly/standalone/deployments/${1}/WEB-INF/
	cp /opt/jboss/jboss-deployment/web-app/jboss-all.xml /opt/jboss/wildfly/standalone/deployments/${1}/WEB-INF/
	rm -rf /opt/jboss/wildfly/standalone/deployments/${1}.war
	jar cvf /opt/jboss/wildfly/standalone/deployments/${1} /opt/jboss/wildfly/standalone/deployments/${1}.war
	jar -cvf /opt/jboss/wildfly/standalone/deployments/${1}.war  -C /opt/jboss/wildfly/standalone/deployments/${1} .
	rm -rf /opt/jboss/wildfly/standalone/deployments/${1}
	
}


chmod 755 /opt/jboss/wildfly/standalone/deployments/*.ear
chmod 755 /opt/jboss/wildfly/standalone/deployments/*.war
addJbossAll user-module-1.6.1 /opt/jboss/jboss-deployment/user/jboss-all.xml 
addJbossAll config-dbaccess-module-3.0.4 /opt/jboss/jboss-deployment/config-db/jboss-all.xml 
addJbossAll config-module-3.0.6 /opt/jboss/jboss-deployment/config/jboss-all.xml
addJbossAll spatial-module-0.5.9 /opt/jboss/jboss-deployment/spatial/jboss-all.xml
addJbossAll reporting-module-0.5.7.presweden1 /opt/jboss/jboss-deployment/reporting/jboss-all.xml
addJbossAll movement-module-3.0.11 /opt/jboss/jboss-deployment/movement/jboss-all.xml
addJbossAll asset-module-3.0.11 /opt/jboss/jboss-deployment/asset/jboss-all.xml
addJbossAll audit-dbaccess-module-3.0.4 /opt/jboss/jboss-deployment/general/jboss-all.xml
addJbossAll audit-module-3.0.6 /opt/jboss/jboss-deployment/audit/jboss-all.xml
addJbossAll exchange-dbaccess-module-3.0.5 /opt/jboss/jboss-deployment/general/jboss-all.xml
addJbossAll exchange-module-3.0.8 /opt/jboss/jboss-deployment/exchange/jboss-all.xml
addJbossAll mobileterminal-module-3.0.11 /opt/jboss/jboss-deployment/mobile/jboss-all.xml
addJbossAll rules-dbaccess-module-3.0.4 /opt/jboss/jboss-deployment/general/jboss-all.xml
addJbossAll rules-module-3.0.3 /opt/jboss/jboss-deployment/rules/jboss-all.xml
addJbossAll naf-module-3.0.0 /opt/jboss/jboss-deployment/plugins/jboss-all.xml
addJbossAll sweagencyemail-module-3.0.0 /opt/jboss/jboss-deployment/plugins/jboss-all.xml
addJbossAll flux-module-2.1.2 /opt/jboss/jboss-deployment/plugins/jboss-all.xml
addJbossAll twostage-module-3.0.0 /opt/jboss/jboss-deployment/plugins/jboss-all.xml
addJbossAll siriusone-module-3.0.0 /opt/jboss/jboss-deployment/plugins/jboss-all.xml
addJbossAlltoWar gs-web-app-2.8.5.presweden1-postgres /opt/jboss/jboss-deployment/web-app/jboss-deployment-structure.xml
addJbossAlltoWar unionvms-web-3.0.5 /opt/jboss/jboss-deployment/web-app/jboss-deployment-structure.xml
addJbossAlltoWar mapfish-print-3.4 /opt/jboss/jboss-deployment/web-app/jboss-deployment-structure.xml
mv /opt/jboss/wildfly/standalone/deployments/gs-web-app-2.8.5.presweden1-postgres.war /opt/jboss/wildfly/standalone/deployments/geoserver.war
mv /opt/jboss/wildfly/standalone/deployments/mapfish-print-3.4.war /opt/jboss/wildfly/standalone/deployments/mapfish-print.war
