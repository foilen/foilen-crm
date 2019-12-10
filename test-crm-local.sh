#!/bin/bash

set -e

# Set environment
export LANG="C.UTF-8"
export VERSION=master-SNAPSHOT

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

# Build
./step-update-copyrights.sh
./step-compile-no-tests.sh
./step-create-docker-image.sh

# Start
USER_ID=$(id -u)
docker run -ti \
  --rm \
  --user $USER_ID \
  --publish 8080:8080 \
  foilen-crm:master-SNAPSHOT \
  --mode LOCAL
