package com.foilen.crm.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class CrmTestConfig {

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }

}
