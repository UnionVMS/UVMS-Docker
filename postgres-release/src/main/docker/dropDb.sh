#!/bin/sh

if [ $# -ne 2 ]; 
    then echo "Requires two arguments [db_name] [postgresql-db-hostname]"
    exit 1
fi

sed  s/db71u/$1/g < drop-all.sql > drop-all-db-$1.sql

echo "Running drop-all.sql"
psql --host=$2 -U postgres -q -f  drop-all-db-$1.sql >/dev/null