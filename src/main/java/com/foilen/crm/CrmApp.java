/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

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
import org.springframework.retry.RetryContext;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.AlwaysRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.context.support.StandardServletEnvironment;

import com.foilen.login.spring.client.security.FoilenLoginSecurityConfig;
import com.foilen.login.stub.spring.client.security.FoilenLoginSecurityStubConfig;
import com.foilen.smalltools.reflection.ReflectionTools;
import com.foilen.smalltools.tools.DirectoryTools;
import com.foilen.smalltools.tools.FileTools;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.LogbackTools;
import com.foilen.smalltools.tools.ResourceTools;
import com.foilen.smalltools.tools.SecureRandomTools;
import com.foilen.smalltools.tools.SpringTools;
import com.google.common.base.Strings;

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
            String mode = options.mode;
            ConfigurableEnvironment environment = new StandardServletEnvironment();
            environment.addActiveProfile(mode);

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

            // Local -> Add some mock values
            if ("LOCAL".equals(mode)) {
                logger.info("Setting some random values for LOCAL mode");

                config.setBaseUrl("http://127.0.0.1:8080");

                config.setMysqlDatabaseUserName("notNeeded");
                config.setMysqlDatabasePassword("notNeeded");

                config.setMailFrom("crm@example.com");

                config.setCompany("MyCompany");

                config.getLoginConfigDetails().setBaseUrl("http://login.example.com");

                config.setLoginCookieSignatureSalt(SecureRandomTools.randomBase64String(10));

            }

            // Override some database configuration if provided via environment
            String overrideMysqlHostName = System.getenv("MYSQL_PORT_3306_TCP_ADDR");
            if (!Strings.isNullOrEmpty(overrideMysqlHostName)) {
                config.setMysqlHostName(overrideMysqlHostName);
            }
            String overrideMysqlPort = System.getenv("MYSQL_PORT_3306_TCP_PORT");
            if (!Strings.isNullOrEmpty(overrideMysqlPort)) {
                config.setMysqlPort(Integer.valueOf(overrideMysqlPort));
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

            // Config per mode
            switch (mode) {
            case "LOCAL":
                break;
            case "PROD":
                // Configure login service
                File loginConfigFile = File.createTempFile("loginConfig", ".json");
                JsonTools.writeToFile(loginConfigFile, config.getLoginConfigDetails());
                System.setProperty("login.cookieSignatureSalt", config.getLoginCookieSignatureSalt());
                System.setProperty("login.configFile", loginConfigFile.getAbsolutePath());

                // Configure database
                System.setProperty("spring.datasource.url", "jdbc:mysql://" + config.getMysqlHostName() + ":" + config.getMysqlPort() + "/" + config.getMysqlDatabaseName());
                System.setProperty("spring.datasource.username", config.getMysqlDatabaseUserName());
                System.setProperty("spring.datasource.password", config.getMysqlDatabasePassword());
                break;
            default:
                System.out.println("Invalid mode: " + mode);
                showUsage();
                return;
            }

            List<Class<?>> sources = new ArrayList<>();

            // Run the upgrader
            if ("LOCAL".equals(mode)) {
                logger.info("Skipping UPGRADE MODE");
            } else {
                logger.info("Begin UPGRADE MODE");
                sources.add(CrmUpgradesSpringConfig.class);

                RetryTemplate infiniteRetryTemplate = new RetryTemplate();

                FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
                fixedBackOffPolicy.setBackOffPeriod(10000L);// 10 seconds
                infiniteRetryTemplate.setBackOffPolicy(fixedBackOffPolicy);

                infiniteRetryTemplate.setRetryPolicy(new AlwaysRetryPolicy());

                infiniteRetryTemplate.execute(new RetryCallback<Void, RuntimeException>() {

                    @Override
                    public Void doWithRetry(RetryContext context) throws RuntimeException {
                        runApp(springBootArgs, sources, mode, true);
                        return null;
                    }
                });

                logger.info("End UPGRADE MODE");
            }

            // Run the main app
            logger.info("Will start the main app");
            sources.clear();

            sources.add(CrmMailConfig.class);

            // small tools
            sources.add(SpringTools.class);

            sources.add(CrmSpringConfig.class);
            sources.add(CrmDbLiveSpringConfig.class);
            sources.add(CrmWebSpringConfig.class);

            // Beans per mode
            switch (mode) {
            case "LOCAL":
                sources.add(FoilenLoginSecurityStubConfig.class);
                break;
            case "PROD":
                // foilen-login-api
                sources.add(FoilenLoginSecurityConfig.class);
                break;
            default:
                System.out.println("Invalid mode: " + mode);
                showUsage();
                return;
            }

            // Start
            runApp(springBootArgs, sources, mode, false);

            // Check if debug
            if (options.debug) {
                LogbackTools.changeConfig("/logback-debug.xml");
            }

        } catch (Exception e) {
            logger.error("Application failed", e);
            System.exit(1);
        }

    }

    private static ConfigurableApplicationContext runApp(List<String> springBootArgs, List<Class<?>> sources, String mode, boolean closeAtEnd) {

        // Set the environment
        ConfigurableEnvironment environment = new StandardServletEnvironment();
        environment.addActiveProfile(mode);
        System.setProperty("MODE", mode);

        SpringApplication springApplication = new SpringApplication();
        springApplication.addPrimarySources(sources);
        springApplication.setEnvironment(environment);
        ConfigurableApplicationContext appCtx = springApplication.run(springBootArgs.toArray(new String[springBootArgs.size()]));
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
