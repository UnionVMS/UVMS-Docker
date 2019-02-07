#!/bin/bash
alias serverlogs="cd /opt/jboss/wildfly/standalone/log/ 
tail -fn 1000 server.log"
alias activitylogs="cd /app/logs/activity 
tail -fn 1000 activity.log"
alias assetlogs="cd /app/logs/asset 
tail -fn 1000 asset.log"
alias exchangelogs="cd /app/logs/exchange 
tail -fn 1000 exchange.log"
alias ruleslogs="cd /app/logs/rules 
tail -fn 1000 rules.log"
alias mdrlogs="cd /app/logs/mdr 
tail -fn 1000 mdr.log"
alias mdrPluginlogs="cd /app/logs/flux-mdr-plugin 
tail -fn 1000 flux-mdr-plugin.log"
alias fluxactivityLogs="cd /app/logs/flux-activity-plugin/ 
tail -fn 1000 flux-activity-plugin.log"
alias movementlogs="cd /app/logs/movement/ 
tail -fn 1000 movement.log"
alias movementpluginlogs="cd /app/logs/movement-plugin/
tail -fn 1000 flux-movement-plugin.log"
alias movplugin="cd /app/logs/flux/ 
tail -fn 1000 flux.log"
alias uploaderlogs="cd /app/logs/uploader 
tail -fn 1000 uploader.log"
alias subscriptionlogs="cd /app/logs/subscription
tail -fn 1000 subscription.log"
alias spatiallongs="cd /app/logs/spatial
tail -fn 1000 spatial.log"
alias configlogs="cd /app/logs/config 
tail -fn 1000 config.log"
alias auditlogs="cd /app/logs/audit 
tail -fn 1000 audit.log"
alias userlogs="cd /app/logs/user 
tail -fn 1000 user.log"
alias inmarsatLogs="cd /app/logs/inmarsat 
tail -fn 1000 inmarsat.log"
alias nafLogs="cd /app/logs/naf 
tail -fn 1000 naf.log"
alias siriusoneLogs="cd /app/logs/siriusone 
tail -fn 1000 siriusone.log"
alias deploymentFolderLS="cd /opt/jboss/wildfly/standalone/deployments
ls -ln"
alias rma="rm -rf asset*"
alias configurationFolder="cd /opt/jboss/wildfly/standalone/configuration/ 
ls -ln"
alias editStandalone="vi /opt/jboss/wildfly/standalone/configuration/standalone-uvms.xml"



