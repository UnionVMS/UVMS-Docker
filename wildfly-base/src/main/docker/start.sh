#!/bin/bash

rm -rf /app/logs/*
rm -rf /opt/jboss/wildfly/standalone/log/*

$JBOSS_HOME/cli-scripts/apply_cli.sh

empty="y"
printf "Waiting for database.\n"
while :
do
   if [[ ${empty} != *"y"* ]];then
   	  printf "Database available. Wildfly starting.\n"
      break
   fi
   empty=`psql -t -A -h postgres -p 5432 -U postgres -d db71u -c "select count(*) from activity.databasechangelog;" 2>/dev/null`
   if [[ -z "${empty// }" ]];then
      empty="y"
   fi
   sleep 5
done

cmd="$@"
exec $cmd
