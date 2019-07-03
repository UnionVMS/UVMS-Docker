#!/bin/bash

rm -rf /app/logs/*
rm -rf /opt/jboss/wildfly/standalone/log/*

echo -e "`/sbin/ip route|awk '/default/ { print $3 }'`\tdocker.host.internal" | sudo tee -a /etc/hosts > /dev/null

locked="y"
printf "Waiting for database.\n"
while :
do
   if [[ ${locked} != *"y"* ]];then
   	  printf "Database available. Wildfly starting.\n"
      break
   fi
   locked=`psql -t -A -h postgres -p 5432 -U postgres -d db71u -c "select locked from activity.databasechangeloglock;" 2>/dev/null`
   if [[ -z "${locked// }" ]];then
      locked="y"
   fi
   sleep 5
done

cmd="$@"
exec $cmd
