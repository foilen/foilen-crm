package com.foilen.crm;

import com.foilen.smalltools.reflection.ReflectionTools;
import com.foilen.smalltools.tools.*;
import com.google.common.base.Strings;
import jakarta.annotation.Nullable;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CrmApp {

    static private final Logger logger = LoggerFactory.getLogger(CrmApp.class);

    public static void configToSystemProperties(CrmConfig config) {
        BeanWrapper configBeanWrapper = new BeanWrapperImpl(config);
        for (PropertyDescriptor propertyDescriptor : configBeanWrapper.getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = configBeanWrapper.getPropertyValue(propertyName);
            if (propertyValue == null || propertyValue.toString().isEmpty()) {
                if (ReflectionTools.findAnnotationByFieldNameAndAnnotation(CrmConfig.class, propertyName, Nullable.class) == null) {
                    System.err.println(propertyName + " in the config cannot be null or empty");
                    System.exit(1);
                }
            } else {
                System.setProperty("crm." + propertyName, propertyValue.toString());
            }
        }
    }

    public static void main(String[] args) {

        try {
            // Get the parameters
            CrmOptions options = new CrmOptions();
            CmdLineParser cmdLineParser = new CmdLineParser(options);
            try {
                cmdLineParser.parseArgument(args);
            } catch (CmdLineException e) {
                e.printStackTrace();
                showUsage();
                return;
            }

            List<String> springBootArgs = new ArrayList<>();
            if (options.debug) {
                springBootArgs.add("--debug");
            }

            // Set the environment
            ConfigurableEnvironment environment = new StandardServletEnvironment();
            environment.addActiveProfile("PROD");

            // Get the configuration from options or environment
            String configFile = options.configFile;
            if (Strings.isNullOrEmpty(configFile)) {
                configFile = environment.getProperty("CONFIG_FILE");
            }
            CrmConfig config;
            if (Strings.isNullOrEmpty(configFile)) {
                config = new CrmConfig();
            } else {
                config = JsonTools.readFromFile(configFile, CrmConfig.class);
            }

            // Override some database configuration if provided via environment
            String overrideMysqlHostName = System.getenv("MYSQL_PORT_3306_TCP_ADDR");
            if (!Strings.isNullOrEmpty(overrideMysqlHostName)) {
                config.setMysqlHostName(overrideMysqlHostName);
            }
            String overrideMysqlPort = System.getenv("MYSQL_PORT_3306_TCP_PORT");
            if (!Strings.isNullOrEmpty(overrideMysqlPort)) {
                config.setMysqlPort(Integer.parseInt(overrideMysqlPort));
            }

            // Check needed config and add it to the known properties
            configToSystemProperties(config);

            // Create the email template directory with default values if provided, but empty
            String emailTemplateDirectory = config.getEmailTemplateDirectory();
            if (!Strings.isNullOrEmpty(emailTemplateDirectory) && !FileTools.exists(emailTemplateDirectory + "/invoice-EN.html")) {
                logger.info("[Email Template] The directory is set, but there is no invoice-EN.html available in it. Will copy default ones");
                DirectoryTools.createPath(emailTemplateDirectory);
                ResourceTools.copyToFile("/com/foilen/crm/services/email/invoice-EN.html", CrmApp.class, new File(emailTemplateDirectory + "/invoice-EN.html"));
                ResourceTools.copyToFile("/com/foilen/crm/services/email/invoice-FR.html", CrmApp.class, new File(emailTemplateDirectory + "/invoice-FR.html"));
                ResourceTools.copyToFile("/com/foilen/crm/services/email/logo.png", CrmApp.class, new File(emailTemplateDirectory + "/logo.png"));
            }

            // Configure login service
            System.setProperty("spring.security.oauth2.client.registration.azure.client-id", config.getLoginAzureConfig().getClientId());
            System.setProperty("spring.security.oauth2.client.registration.azure.client-secret", config.getLoginAzureConfig().getClientSecret());
            System.setProperty("spring.security.oauth2.client.registration.azure.redirect-uri", config.getLoginAzureConfig().getRedirectUri());

            // Configure database
            System.setProperty("spring.datasource.url", "jdbc:mysql://" + config.getMysqlHostName() + ":" + config.getMysqlPort() + "/" + config.getMysqlDatabaseName());
            System.setProperty("spring.datasource.username", config.getMysqlDatabaseUserName());
            System.setProperty("spring.datasource.password", config.getMysqlDatabasePassword());

            List<Class<?>> sources = new ArrayList<>();

            // Run the upgrader
            logger.info("Begin UPGRADE MODE");
            sources.add(CrmUpgradesSpringConfig.class);

            RetryTemplate infiniteRetryTemplate = new RetryTemplate();

            FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
            fixedBackOffPolicy.setBackOffPeriod(10000L);// 10 seconds
            infiniteRetryTemplate.setBackOffPolicy(fixedBackOffPolicy);

            infiniteRetryTemplate.setRetryPolicy(new AlwaysRetryPolicy());

            infiniteRetryTemplate.execute((RetryCallback<Void, RuntimeException>) context -> {
                runApp(springBootArgs, sources, true);
                return null;
            });

            logger.info("End UPGRADE MODE");

            // Run the main app
            logger.info("Will start the main app");
            sources.clear();

            sources.add(CrmMailConfig.class);

            // small tools
            sources.add(SpringTools.class);

            sources.add(CrmSpringConfig.class);
            sources.add(CrmDbLiveSpringConfig.class);
            sources.add(CrmWebSpringConfig.class);
            sources.add(CrmSecuritySpringConfig.class);

            // Start
            runApp(springBootArgs, sources, false);

            // Check if debug
            if (options.debug) {
                LogbackTools.changeConfig("/logback-debug.xml");
            }

        } catch (Exception e) {
            logger.error("Application failed", e);
            System.exit(1);
        }

    }

    private static ConfigurableApplicationContext runApp(List<String> springBootArgs, List<Class<?>> sources, boolean closeAtEnd) {

        // Set the environment
        ConfigurableEnvironment environment = new StandardServletEnvironment();
        environment.addActiveProfile("PROD");
        System.setProperty("MODE", "PROD");

        // Get the port to use
        var port = SystemTools.getPropertyOrEnvironment("HTTP_PORT", "8080");
        System.setProperty("server.port", port);

        SpringApplication springApplication = new SpringApplication();
        springApplication.addPrimarySources(sources);
        springApplication.setEnvironment(environment);

        // Exclude some auto-configuration
        System.setProperty("spring.autoconfigure.exclude", "com.azure.spring.cloud.autoconfigure.implementation.context.AzureTokenCredentialAutoConfiguration");
        ConfigurableApplicationContext appCtx = springApplication.run(springBootArgs.toArray(new String[0]));
        if (closeAtEnd) {
            appCtx.close();
        }
        return appCtx;
    }

    private static void showUsage() {
        System.out.println("Usage:");
        CmdLineParser cmdLineParser = new CmdLineParser(new CrmOptions());
        cmdLineParser.printUsage(System.out);
    }

}
