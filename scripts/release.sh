#!/usr/bin/env bash

# Usage release.sh RELEASE_VERSION DEVELOPMENT_VERSION
# Example: relase.sh 1.0.4 1.0.5-SNAPSHOT
set -e -x

BASE_DIR=$(readlink -f $(dirname $0)/..)
DOCKER_DIR=${BASE_DIR}/docker

RELEASE_VERSION=${1}
DEVELOPMENT_VERSION=${2}

if test "${RELEASE_VERSION}" == "" -o "${DEVELOPMENT_VERSION}" == ""
then
    echo "No version provided"
    exit 1
fi

cd ${BASE_DIR}

./mvnw versions:set -DnewVersion=${RELEASE_VERSION}
./mvnw versions:commit
git add .
git commit -m "Release ${RELEASE_VERSION}"
git tag ${RELEASE_VERSION}

./mvnw versions:set -DnewVersion=${DEVELOPMENT_VERSION}
./mvnw versions:commit
git add .
git commit -m "Switch version to ${DEVELOPMENT_VERSION}"
