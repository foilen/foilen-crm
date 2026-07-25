#!/bin/bash

set -e

# Set environment
export LANG="C.UTF-8"
export VERSION=master-SNAPSHOT

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

# Build
echo '###[ Create build ]###'
./step-compile-no-tests.sh
./step-create-docker-image.sh

FOLDER_DATA=$PWD/_data
mkdir -p $FOLDER_DATA

# Start MongoDB (single-node replica set, needed for transactions)
INSTANCE=crm_db
DBNAME=crm

if ! docker ps | grep $INSTANCE ; then
	echo '###[ Start MongoDB ]###'
	docker run \
	  --rm \
	  --name $INSTANCE \
	  --volume $FOLDER_DATA/mongo:/data/db \
	  -p 27017:27017 \
	  -d mongo:8.2 --replSet rs0 --bind_ip_all

  echo '###[ Wait 10 seconds ]###'
  sleep 10s

  echo '###[ Initiate the replica set ]###'
  docker exec -i $INSTANCE mongosh --eval "rs.initiate()"
fi

# Config file
cat > $FOLDER_DATA/config.json << _EOF
{
	"baseUrl" : "http://127.0.0.1:8080",

	"mongoUri" : "mongodb://host.docker.internal:27017/?replicaSet=rs0",
	"mongoDatabase" : "$DBNAME",

	"mailHost" : "127.0.0.1",
	"mailPort" : 25,
	"mailStartTlsEnable" : false,

	"mailFrom" : "crm@localhost",

	"company" : "MyCompany",

	"loginAzureConfig" : {
		"clientId" : "XXXXX",
		"clientSecret" : "XXXXX",
		"redirectUri" : "http://xxxxxxxx/login/oauth2/code/azure"
	},
	"loginCookieSignatureSalt" : "AAA",

	"emailTemplateDirectory" : "/data/emailTemplate"
}
_EOF

# Start
echo '###[ Start UI ]###'
USER_ID=$(id -u)
docker run -ti \
  --rm \
  --env CONFIG_FILE=/data/config.json \
  --user $USER_ID \
  --volume $FOLDER_DATA:/data \
  --publish 8080:8080 \
  --add-host host.docker.internal:host-gateway \
  foilen-crm:master-SNAPSHOT
