#!/bin/bash

set -m

# Start couchbase server
/entrypoint.sh couchbase-server &

# Check if couchbase server is up
check_db() {
  curl --silent http://127.0.0.1:8091/pools > /dev/null
  echo $?
}

# Wait until it's ready
until [[ $(check_db) = 0 ]]; do
  >&2 echo "Waiting for Couchbase Server to be available"
  sleep 1
done

echo "*** Couchbase Server its Ready to be Configured! ***"

couchbase-cli cluster-init -c 127.0.0.1:8091 \
 --cluster-username ${USERNAME} \
 --cluster-password ${PASSWORD} \
 --services data,index,query,fts\
 --cluster-ramsize ${SERVER_MEMORY_QUOTA} \
 --cluster-index-ramsize ${INDEX_MEMORY_QUOTA} \
 --index-storage-setting default

couchbase-cli bucket-create -c 127.0.0.1:8091 \
 --password ${PASSWORD} \
 --username ${USERNAME} \
 --bucket ${COUNTERS_BUCKET} \
 --bucket-type couchbase \
 --bucket-ramsize ${COUNTERS_BUCKET_MEMORY_QUOTA} \
 --bucket-eviction-policy valueOnly \
 --enable-index-replica 0 \
 --bucket-replica 0 \
 --wait

couchbase-cli bucket-create -c 127.0.0.1:8091 \
 --password ${PASSWORD} \
 --username ${USERNAME} \
 --bucket ${BUCKET} \
 --bucket-type couchbase \
 --bucket-ramsize ${BUCKET_MEMORY_QUOTA} \
 --bucket-eviction-policy valueOnly \
 --enable-index-replica 0 \
 --bucket-replica 0 \
 --wait

echo "*** Insert initial config ***"
curl http://${USERNAME}:${PASSWORD}@127.0.0.1:8093/query/service -d "statement=INSERT INTO ${COUNTERS_BUCKET} (KEY, VALUE) VALUES ( \"meli-proxy-config\",  {\"by_ip\":{\"192.168.1.74\":5000},\"by_path\":{\"/users/.*\":1000},\"by_ip_and_path\":{\"192.168.1.74\":{\"/sites\":5000,\"/users/.*\":5000}},\"default_limit\":null,\"version\":1}) RETURNING *"
curl http://${USERNAME}:${PASSWORD}@127.0.0.1:8093/query/service -d "statement=INSERT INTO ${COUNTERS_BUCKET} (KEY, VALUE) VALUES ( \"meli-proxy-blocked\",  {\"by_ip\":[],\"by_path\":[],\"by_ip_and_path\":[]}) RETURNING *"

echo "*** Creating Indexes ***"
curl http://${USERNAME}:${PASSWORD}@127.0.0.1:8093/query/service -d "statement=CREATE INDEX date_index ON ${BUCKET}(date) USING GSI"
curl http://${USERNAME}:${PASSWORD}@127.0.0.1:8093/query/service -d "statement=CREATE INDEX path_index ON ${BUCKET}(`path`) USING GSI"
curl http://${USERNAME}:${PASSWORD}@127.0.0.1:8093/query/service -d "statement=CREATE INDEX ip_index ON ${BUCKET}(ip) USING GSI"

echo "************************************************"
echo "******* Couchbase Started and Configured *******"
echo "************************************************"

fg 1