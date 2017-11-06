#!bin/sh

echo "Running setup.sql"
psql --single-transaction -q -f  setup.sql >/dev/null