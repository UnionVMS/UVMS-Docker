#!/bin/bash
locked="y"
printf "Waiting for database.\n"
while :
do
   if [[ ${locked} != *"y"* ]];then
   	  printf "Database available. Wildfly starting.\n"
      break
   fi
   locked=`psql -t -A -h postgres -p 5432 -U postgres -d db71u -c "select locked from usm.databasechangeloglock;" 2>/dev/null`
   if [[ -z "${locked// }" ]];then
      locked="y"
   fi
   sleep 5
done

cmd="$@"
exec $cmd
