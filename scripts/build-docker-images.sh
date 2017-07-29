#!/bin/bash

set -e

BASE_DIR=$(readlink -f $(dirname $0)/..)
DOCKER_DIR=${BASE_DIR}/docker

cd ${BASE_DIR}
./mvnw -Poracle -Pmysql -Ppostgres clean install

rm -f ${DOCKER_DIR}/*/*.jar
cp ${BASE_DIR}/pan-discovery-db/target/pan-discovery-db-*.jar ${DOCKER_DIR}/db/
cp ${BASE_DIR}/pan-discovery-fs/target/pan-discovery-fs-*.jar ${DOCKER_DIR}/fs/

cd ${DOCKER_DIR}/db/
docker build -t alcibiade/pan-discovery-db .

cd ${DOCKER_DIR}/fs/
docker build -t alcibiade/pan-discovery-fs .

