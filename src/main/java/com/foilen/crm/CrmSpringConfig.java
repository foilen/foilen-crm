package com.foilen.crm;

import com.foilen.crm.exception.CrmException;
import com.foilen.crm.localonly.EmailServiceMock;
import com.foilen.crm.localonly.FakeDataService;
import com.foilen.crm.localonly.FakeDataServiceImpl;
import com.foilen.crm.localonly.LocalLaunchService;
import com.foilen.smalltools.restapi.services.PaginationService;
import com.foilen.smalltools.restapi.services.PaginationServiceImpl;
import com.foilen.smalltools.tools.AbstractBasics;
import com.google.common.base.Strings;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.wiring.BeanConfigurerSupport;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@ComponentScan({"com.foilen.crm.db.repository",
        "com.foilen.crm.services",
        "com.foilen.crm.tasks"
})
@EnableAutoConfiguration
@EnableScheduling
@PropertySource({"classpath:/com/foilen/crm/application-common.properties", "classpath:/com/foilen/crm/application-${MODE}.properties"})
public class CrmSpringConfig extends AbstractBasics {

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

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/com/foilen/crm/messages/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public PaginationService paginationService() {
        return new PaginationServiceImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Configuration
    @Profile({"JUNIT", "LOCAL"})
    public static class CrmConfigLocal {

        @Bean
        public FakeDataService fakeDataService() {
            return new FakeDataServiceImpl();
        }

        @Bean
        public LocalLaunchService localLaunchService(Environment environment) {
            // The LOCAL profile keeps the users list empty so that the first person to log in becomes admin
            boolean includeUsers = environment.matchesProfiles("JUNIT");
            return new LocalLaunchService(fakeDataService(), includeUsers);
        }

    }

}
