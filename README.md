# About

A simple CRM system.

## What makes it simple

- Only for a single company
-- If you need multiple ones, create another instance under another url
- Basic functionalities
-- If you have a complex use cases, use the API instead of adding more features

# Usage

## Configuration environment

Some configuration options can be overridden with the environment variables:

* `CONFIG_FILE` : The path to the config file
* `MYSQL_PORT_3306_TCP_ADDR` : To change `mysqlHostName` (used by Docker Links)
* `MYSQL_PORT_3306_TCP_PORT` : To change `mysqlPort` (used by Docker Links)

## Configuration file

You need to create a json configuration file that maps the class CrmConfig.

Here is an example of the content:

```json
{
	"baseUrl" : "http://127.0.0.1:8888",
	
	"mysqlHostName" : "127.0.0.1",
	"mysqlPort" : 3306,
	"mysqlDatabaseName" : "crm",
	"mysqlDatabaseUserName" : "root",
	"mysqlDatabasePassword" : "ABC",

	"mailHost" : "127.0.0.1",
	"mailPort" : 25,
	"mailUsername" : "user",
	"mailPassword" : "password",
	
	"mailFrom" : "crm@localhost",
	
	"company" : "MyCompany",
	
	"loginConfigDetails" : {
		"appId" : "AAAAA",
		"baseUrl" : "https://login.foilen-lab.com"
	},
	"loginCookieSignatureSalt" : "AAA",
	
	"emailTemplateDirectory" : "/data/emailTemplate"
}
```

You can then specify the full path of that file as the *configFile* argument when launching the app or as the
*CONFIG_FILE* environment variable.

## Admin

The first user that logs in is an administrator.

## Change email templates

By specifying an `emailTemplateDirectory` in the config file, on the first run, it will copy the default 
templates in it. You can then modify them and restart the application.

# Development

## Text messages

The translations are in:
- /src/main/resources/WEB-INF/crm/messages/messages_en.properties
- /src/main/resources/WEB-INF/crm/messages/messages_en.properties

And when you add more, you can easily sort them by running `SortMessagesApp.launch`.

## To modify UI vendor libraries  

Edit `src/main/resources/WEB-INF/crm/web/js/vendor/package.json`.

Run `./update-ui-libraries.sh`.

If you are adding or removing dependencies, you can then edit `CrmWebSpringConfig`.

## Local in Eclipse

Run *CrmApp - LOCAL.launch*

## Local in Docker

Simply execute `./test-crm-local.sh` .

## TEST in Docker

Simply execute `./test-crm-test.sh` .

When done, cleanup by stopping the DB: `docker stop crm_db` .

# More

## Swagger

You can see the API documentation here: http://localhost:8888/swagger-ui/index.html

