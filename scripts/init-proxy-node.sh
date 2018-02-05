#!/bin/bash

nohup java -Xms256M \
 -Xmx512M \
 -Djava.security.egd=file:/dev/./urandom \
 -XX:+TieredCompilation \
 -XX:+UseG1GC \
 -XX:+CMSClassUnloadingEnabled \
 -XX:NewRatio=2 \
 -cp lib/*:. com.ml.meliproxy.service.WebServer $1 $2 >output_$1.log 2>&1 &