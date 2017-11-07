#!/bin/sh

if [ $# -ne 1 ]; 
    then echo "Requires one argument postgresql-db-hostname"
    exit 1
fi

echo "Running drop-all.sql"
psql --host=$1 -U postgres --single-transaction -q -f  drop-all.sql >/dev/null