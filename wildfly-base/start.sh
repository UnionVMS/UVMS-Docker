#!/bin/sh
delay=$1
shift
cmd="$@"
echo "Waiting $delay seconds before starting Wildfly with command $cmd"
sleep $delay

exec $cmd