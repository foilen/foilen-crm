/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2022 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.wiring.BeanConfigurerSupport;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.foilen.crm.exception.CrmException;
import com.foilen.crm.localonly.EmailServiceMock;
import com.foilen.crm.localonly.FakeDataService;
import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.localonly.LocalLaunchService;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.CharsetTools;
import com.google.common.base.Strings;

import freemarker.template.TemplateExceptionHandler;

@Configuration
@ComponentScan({ "com.foilen.crm.db.dao", //
        "com.foilen.crm.services", //
        "com.foilen.crm.tasks" //
})
@EnableAutoConfiguration
@EnableScheduling
@PropertySource({ "classpath:/com/foilen/crm/application-common.properties", "classpath:/com/foilen/crm/application-${MODE}.properties" })
public class CrmSpringConfig extends AbstractBasics {

    @Configuration
    @Profile({ "PROD" })
    public static class ConfigUiConfigProd {
        @Bean
        public MessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:/WEB-INF/crm/messages/messages");
            messageSource.setDefaultEncoding(CharsetTools.UTF_8.name());
            messageSource.setUseCodeAsDefaultMessage(true);
            return messageSource;
        }
    }

    @Configuration
    @Profile({ "JUNIT", "LOCAL" })
    public static class CrmConfigLocal {
        @Primary
        @Bean
        public EmailServiceMock emailServiceMock() {
            return new EmailServiceMock();
        }

        @Bean
        public FakeDataService fakeDataService() {
            return new FakeDataServiceImpl();
        }

        @Bean
        public LocalLaunchService localLaunchService() {
            return new LocalLaunchService(fakeDataService());
        }

        @Bean
        public MessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.setBasename("classpath:/WEB-INF/crm/messages/messages");
            messageSource.setDefaultEncoding(CharsetTools.UTF_8.name());
            messageSource.setUseCodeAsDefaultMessage(true);
            return messageSource;
        }

    }

    @Value("${crm.emailTemplateDirectory:#{null}}")
    private String emailTemplateDirectory;

    @Bean
    public BeanConfigurerSupport beanConfigurerSupport() {
        return new BeanConfigurerSupport();
    }

    @Bean
    public freemarker.template.Configuration freemarkerConfiguration() {
        freemarker.template.Configuration freemarkerConfiguration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_29);

        // Get the right path
        if (Strings.isNullOrEmpty(emailTemplateDirectory)) {
            logger.info("Using default email template");
            freemarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/com/foilen/crm/services/email/");
        } else {
            try {
                logger.info("Using {} for email template", emailTemplateDirectory);
                freemarkerConfiguration.setDirectoryForTemplateLoading(new File(emailTemplateDirectory));
            } catch (IOException e) {
                throw new CrmException("Problem configuring email template", e);
            }
        }

        freemarkerConfiguration.setDefaultEncoding("UTF-8");
        freemarkerConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return freemarkerConfiguration;
    }

}
