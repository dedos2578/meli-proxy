# Meli Proxy

Proxy con control de acceso y estadísticas.

## Ambiente
Ejecutar el docker que contiene el couchbase a utilizar (tener en cuenta que reserva 1GB de RAM al inicializar):
 * Parados en el directorio _docker_ ejecutar los siguientes comandos: 
 ```
    docker build . --tag meli-proxy-couchbase
    docker run -d --name meli-proxy-couchbase -p 8091-8094:8091-8094 -p 11210:11210 meli-proxy-couchbase
```
 * Monitorear los logs del docker (_docker logs meli-proxy-couchbase -f_) y esperar el mensaje _"Couchbase Started and Configured"_
 * Para acceder al couchbase se debe de entar al siguiente link: http://127.0.0.1:8091/ui/index.html (Usuario: admin Password: password)
 
## Ejecución
### meli-proxy-service
Para ejecutar la aplicación es necesario pasarle dos argumentos, el puerto en el que se desea que esta exponga el proxy y el path al cual queremos redireccionar.
Si se ejecutan varias instancias de la aplicación (con distinto puerto) en el mismo equipo, esta formara un cluster automaticamente entre estas.

### meli-proxy-reports
Para ejecutar la aplicación es necesario pasarle un argumento, el puerto en el que se desea que esta expongan los servicios de reporte y estadisticas.
