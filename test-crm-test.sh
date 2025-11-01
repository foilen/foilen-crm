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

# Start mariadb
INSTANCE=crm_db
DBNAME=crm

if ! docker ps | grep $INSTANCE ; then
	echo '###[ Start mariadb ]###'
	docker run \
	  --rm \
	  --name $INSTANCE \
	  --env MYSQL_ROOT_PASSWORD=ABC \
	  --env DBNAME=$DBNAME \
	  --volume $FOLDER_DATA:/data \
	  -d mariadb:10.5.8
  
  echo '###[ Wait 20 seconds ]###'
  sleep 20s
fi
echo '###[ Create the MariaDB database ]###'
until docker exec -i $INSTANCE mysql -uroot -pABC << _EOF
  CREATE DATABASE IF NOT EXISTS $DBNAME;
_EOF
do
sleep 5
done

# Config file
cat > $FOLDER_DATA/config.json << _EOF
{
	"baseUrl" : "http://127.0.0.1:8080",
	
	"mysqlDatabaseName" : "crm",
	"mysqlDatabaseUserName" : "root",
	"mysqlDatabasePassword" : "ABC",

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

echo '###[ Start phpMyAdmin ]###'
docker run --rm --name ${INSTANCE}_phpmyadmin -d --link ${INSTANCE}:db -p 12345:80 phpmyadmin/phpmyadmin
echo 'phpMyAdmin on http://127.0.0.1:12345/ with user "root" and password "ABC"'

# Start
echo '###[ Start UI ]###'
USER_ID=$(id -u)
docker run -ti \
  --rm \
  --env CONFIG_FILE=/data/config.json \
  --user $USER_ID \
  --volume $FOLDER_DATA:/data \
  --publish 8080:8080 \
  --link ${INSTANCE}:mysql \
  foilen-crm:master-SNAPSHOT
