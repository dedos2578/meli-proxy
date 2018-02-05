#!/bin/bash

for pid in `ps -ef | grep java | grep WebServer | grep -v grep | awk '{print $2}'`; do
  echo 'Killing '$pid
  kill -9 $pid
done
