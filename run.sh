#!/usr/bin/env bash
ROOT_DIR="$( cd -P "$( dirname "$SOURCE" )" >/dev/null 2>&1 && pwd )"
HOST="localhost"

# Define env
network="wago"
grafana_container="grafana-instance"
influxdb_container="influxdb-instance"
grafana_volume_storage="grafana-storage"

# Clean workspace
docker stop $grafana_container &>/dev/null || true
docker stop $influxdb_container &>/dev/null || true
docker network rm "$network" &>/dev/null || true

# Create network
echo "\033[0;32m - Creating network:\033[0m " && docker network create -d bridge "$network"

# Create storage
echo "\033[0;32m - Creating volume:\033[0m " && docker volume create $grafana_volume_storage

# Run grafana && influxdb
echo "\033[0;32m - Running grafana:\033[0m " && docker run --rm -d -p 3000:3000 \
--name=$grafana_container \
--network="$network" \
-v $grafana_volume_storage:/var/lib/grafana grafana/grafana
echo "\033[0;32m - Running influxdb:\033[0m " && docker run --rm -d -p 8086:8086 \
--name=$influxdb_container \
--network=$network \
-v $ROOT_DIR/influxdb.conf:/etc/influxdb/influxdb.conf:ro influxdb \
-config /etc/influxdb/influxdb.conf

docker ps