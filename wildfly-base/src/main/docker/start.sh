#!/bin/bash

# Remove all previous logs
rm -rf /app/logs/*
rm -rf /opt/jboss/wildfly/standalone/log/*

# Check if the db is up, if not wait for 5 seconds and check again
locked="y"
printf "Waiting for database.\n"
while :
do
   if [[ ${locked} != *"y"* ]];then
   	  printf "Database available. Wildfly starting.\n"
      break
   fi
   locked=`psql -t -A -h postgres -p 5432 -U postgres -d db71u -c "select count(*) from usm.databasechangelog;" 2>/dev/null`
   if [[ -z "${locked// }" ]];then
      locked="y"
   fi
   sleep 5
done

if [ -n "`grep %ECAS_ $JBOSS_HOME/modules/eu/europa/ec/ecas/main/ecas-config.properties`" ]; then
   sed -i -e "s#%ECAS_SERVICE_URL%#$ECAS_SERVICE_URL#" -e "s#%ECAS_CERTIFICATE_ESCAPED%#$ECAS_CERTIFICATE_ESCAPED#" -e "s#%ECAS_BASE_URL%#$ECAS_BASE_URL#" $JBOSS_HOME/modules/eu/europa/ec/ecas/main/ecas-config.properties
fi

cmd="$@"
exec $cmd
