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

* `HTTP_PORT` : The port to listen on (default 8080)
* `CONFIG_FILE` : The path to the config file
* `MYSQL_PORT_3306_TCP_ADDR` : To change `mysqlHostName` (used by Docker Links)
* `MYSQL_PORT_3306_TCP_PORT` : To change `mysqlPort` (used by Docker Links)

## Configuration file

You need to create a json configuration file that maps the class CrmConfig.

Here is an example of the content:

```json
{
	"baseUrl" : "http://127.0.0.1:8080",

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

	"loginAzureConfig" : {
		"clientId" : "XXXXX",
		"clientSecret" : "XXXXX",
		"redirectUri" : "http://xxxxxxxx/login/oauth2/code/azure"
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
- src/main/resources/com/foilen/crm/messages/messages_en.properties
- src/main/resources/com/foilen/crm/messages/messages_en.properties

And when you add more, you can easily sort them by running `SortMessagesApp`.

## Frontend Development

The frontend is built with:
- React
- Bootstrap 5.3.6

### Frontend Structure

The frontend code is located in the `src/main/ui` directory with the following structure:

- `src/main/ui/src/components`: Reusable React components
- `src/main/ui/src/utils`: Utility functions
- `src/main/ui/src/views`: Page components
- `src/main/ui/src/App.js`: Main application component
- `src/main/ui/src/index.js`: Application entry point

### Running the Frontend in Development Mode

To run the frontend in development mode, first start the `CrmApp` locally and then `./gradlew npmRunWatch`.

## TEST in IntelliJ

Run `./mariadb-start.sh`

Create a config file with your Azure details by following the instructions in "Configuration file" above and save it as `test-config.json`.

Then run "CrmApp" in IntelliJ.

## TEST in Docker

Simply execute `./test-crm-test.sh` .

Then go on http://127.0.0.1:8080/ .

When done, cleanup by stopping the DB: `docker stop crm_db crm_db_phpmyadmin` .

# More

## Swagger

You can see the API documentation here: http://127.0.0.1:8080/swagger-ui/index.html
