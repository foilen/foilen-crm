#!/bin/bash

set -e

INSTANCE=mariadb-crm
DBNAME=foilen_crm

echo START MariaDB
docker run \
  --rm \
  --name $INSTANCE \
  -e MYSQL_ROOT_PASSWORD=ABC \
  -e DBNAME=$DBNAME \
  -p 3306:3306 \
  -d mariadb:10.5.8

echo "Create database (might fail a couple times)"
until docker exec -i $INSTANCE mysql -uroot -pABC << _EOF
  CREATE DATABASE foilen_crm;
  CREATE DATABASE foilen_crm_junits;
_EOF
do
sleep 5
done
echo [DONE] Database created 

echo START phpMyAdmin
docker run --rm --name ${INSTANCE}_phpmyadmin -d --link ${INSTANCE}:db -p 12345:80 phpmyadmin/phpmyadmin
echo 'phpMyAdmin on http://127.0.0.1:12345/ with user "root" and password "ABC"'
