#!/bin/sh

if [ $# -ne 2 ]; 
    then echo "Requires two arguments [db_name] [postgresql-db-hostname]"
    exit 1
fi

echo "Running drop-all.sql"
psql --host=$1 -U postgres -q -f  drop-all.sql >/dev/null