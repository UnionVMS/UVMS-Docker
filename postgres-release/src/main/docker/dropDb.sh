#!bin/sh

echo "Running drop-all.sql"
psql --single-transaction -q -f  drop-all.sql >/dev/null