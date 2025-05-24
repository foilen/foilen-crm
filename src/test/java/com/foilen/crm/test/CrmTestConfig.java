package com.foilen.crm.test;

import com.azure.spring.cloud.autoconfigure.implementation.context.AzureTokenCredentialAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
@SpringBootApplication(exclude = {AzureTokenCredentialAutoConfiguration.class})
public class CrmTestConfig {

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

}
