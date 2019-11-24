#!/usr/bin/env bash
docker rmi -f search-cli-poc:latest &>/dev/null || true 
docker stop runningSearchContainer &>/dev/null || true 
docker build -t search-cli-poc:latest .
docker run \
-p 4000:4000 \
-v $PWD:/root/search-cli-poc/workspace \
-it --rm --name runningSearchContainer search-cli-poc