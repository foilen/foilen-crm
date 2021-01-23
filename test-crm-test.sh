#!/bin/bash

set -e

# Set environment
export LANG="C.UTF-8"
export VERSION=master-SNAPSHOT

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

# Build
echo '###[ Create build ]###'
./step-update-copyrights.sh
./step-compile-no-tests.sh
./step-create-docker-image.sh

FOLDER_DATA=$PWD/_data
mkdir -p $FOLDER_DATA

# Start mariadb
INSTANCE=crm_db
DBNAME=crm

cat > $FOLDER_DATA/createDb.sh << _EOF
#!/bin/bash
mysql -uroot -pABC << _EOFF
  CREATE DATABASE $DBNAME;
_EOFF
_EOF
chmod +x $FOLDER_DATA/createDb.sh

if ! docker ps | grep $INSTANCE ; then
	echo '###[ Start mariadb ]###'
	docker run \
	  --rm \
	  --name $INSTANCE \
	  --env MYSQL_ROOT_PASSWORD=ABC \
	  --env DBNAME=$DBNAME \
	  --volume $FOLDER_DATA:/data \
	  -d mariadb:10.3.6
  
  echo '###[ Wait 20 seconds ]###'
  sleep 20s
  echo '###[ Create the MariaDB database ]###'
  docker exec -ti $INSTANCE /data/createDb.sh
fi

# Config file
cat > $FOLDER_DATA/config.json << _EOF
{
	"baseUrl" : "http://127.0.0.1:8888",
	
	"mysqlDatabaseName" : "crm",
	"mysqlDatabaseUserName" : "root",
	"mysqlDatabasePassword" : "ABC",

	"mailHost" : "127.0.0.1",
	"mailPort" : 25,
	
	"mailFrom" : "crm@localhost",
	
	"company" : "MyCompany",
	
	"loginConfigDetails" : {
		"appId" : "AAAAA",
		"baseUrl" : "https://login.foilen-lab.com"
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
  --publish 8888:8080 \
  --link ${INSTANCE}:mysql \
  foilen-crm:master-SNAPSHOT

