package com.foilen.crm.test;

import com.foilen.crm.localonly.EmailServiceMock;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
@SpringBootApplication
public class CrmTestConfig {

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

    @Primary
    @Bean
    public EmailServiceMock emailServiceMock() {
        return new EmailServiceMock();
    }

}
