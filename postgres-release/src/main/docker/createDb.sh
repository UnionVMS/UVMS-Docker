#!/bin/sh

if [ $# -ne 2 ]; 
    then echo "Requires two arguments [db_name] [postgresql-db-hostname]"
    exit 1
fi

sed -i 's/db71u/$1/g' < setup.sql > setup-$1.sql

echo "Running setup.sql"
psql --host=$2 -U postgres  -q -f  setup-$1.sql >/dev/null