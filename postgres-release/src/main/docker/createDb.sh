#!/bin/sh

if [ $# -ne 2 ]; 
    then echo "Requires two arguments [db_name] [postgresql-db-hostname]"
    exit 1
fi

echo "Running setup.sql"
psql --host=$1 -U postgres  -q -f  setup.sql >/dev/null