#!/bin/bash

set -e

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

echo ----[ Prepare folder for docker image ]----
DOCKER_BUILD=$RUN_PATH/build/docker

rm -rf $DOCKER_BUILD
mkdir -p $DOCKER_BUILD/app

cp -v build/distributions/foilen-crm-$VERSION.zip $DOCKER_BUILD/app/foilen-crm.zip
cp -v docker-release/* $DOCKER_BUILD
echo -n $VERSION > $DOCKER_BUILD/app/version.txt

cd $DOCKER_BUILD/app
unzip foilen-crm.zip
rm foilen-crm.zip
mv foilen-crm-$VERSION/* .
rm -rf foilen-crm-$VERSION

echo ----[ Docker image folder content ]----
find $DOCKER_BUILD

echo ----[ Build docker image ]----
DOCKER_IMAGE=foilen-crm:$VERSION
docker build -t $DOCKER_IMAGE $DOCKER_BUILD
docker tag $DOCKER_IMAGE foilen/$DOCKER_IMAGE

rm -rf $DOCKER_BUILD
