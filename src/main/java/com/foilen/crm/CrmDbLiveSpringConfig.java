/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.foilen.crm.CrmDbLiveSpringConfig.ConfigUiDbLiveConfigLocal;
import com.foilen.crm.CrmDbLiveSpringConfig.ConfigUiDbLiveConfigProd;

@Configuration
@Import({ ConfigUiDbLiveConfigLocal.class, ConfigUiDbLiveConfigProd.class })
@EnableTransactionManagement
public class CrmDbLiveSpringConfig {

    @Configuration
    @Profile({ "JUNIT", "LOCAL" })
    public static class ConfigUiDbLiveConfigLocal {
        // Uses Spring Boot datasource
    }

    @Configuration
    @Profile({ "PROD" })
    @PropertySource({ "classpath:/com/foilen/crm/application-common.properties", "classpath:/com/foilen/crm/application-${MODE}.properties" })
    public static class ConfigUiDbLiveConfigProd {
        // Uses Spring Boot datasource
    }

}
