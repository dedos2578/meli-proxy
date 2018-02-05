nohup java -Xms256M \
 -Xmx512M \
 -Djava.security.egd=file:/dev/./urandom \
 -XX:+TieredCompilation \
 -XX:+UseG1GC \
 -XX:+CMSClassUnloadingEnabled \
 -XX:NewRatio=2 \
 -cp reports_lib/*:. com.ml.meliproxy.reports.WebServer 9000 >reports_output.log 2>&1 &