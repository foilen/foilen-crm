/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm;

import java.util.List;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceChainRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import com.foilen.crm.web.controller.GeneralHandlerExceptionResolver;
import com.foilen.crm.web.controller.SwaggerExpose;
import com.foilen.smalltools.spring.resourceresolver.BundleResourceResolver;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
@ComponentScan({ "com.foilen.crm.web" })
public class CrmWebSpringConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/WEB-INF/crm/web/");

        boolean isProd = "PROD".equals(System.getProperty("MODE"));

        ResourceChainRegistration chain = registry.addResourceHandler("/bundles/**") //
                .setCachePeriod(365 * 24 * 60 * 60) //
                .resourceChain(isProd) //
                .addResolver(new EncodedResourceResolver()); //
        if (isProd) {
            chain.addResolver(new CachingResourceResolver(new ConcurrentMapCache("bundles")));
        }
        BundleResourceResolver bundleResourceResolver = new BundleResourceResolver().setCache(isProd) //
                .setGenerateGzip(true);

        bundleResourceResolver.addBundleResource("all-vendors.css", "/WEB-INF/crm/web/css/vendors/bootstrap-4.6.0.min.css");

        bundleResourceResolver.addBundleResource("all-vendors.js", "/WEB-INF/crm/web/js/vendors/jquery-3.5.1.min.js");
        bundleResourceResolver.addBundleResource("all-vendors.js", "/WEB-INF/crm/web/js/vendors/js-cookie-2.2.1.min.js");
        bundleResourceResolver.addBundleResource("all-vendors.js", "/WEB-INF/crm/web/js/vendors/bootstrap-4.6.0.bundle.min.js");
        if (isProd) {
            bundleResourceResolver.addBundleResource("all-vendors.js", "/WEB-INF/crm/web/js/vendors/vue-2.6.12.min.js");
        } else {
            bundleResourceResolver.addBundleResource("all-vendors.js", "/WEB-INF/crm/web/js/vendors/vue-2.6.12-dev.js");
        }
        bundleResourceResolver.addBundleResource("all-vendors.js", "/WEB-INF/crm/web/js/vendors/vue-router-3.4.9.js");
        bundleResourceResolver.addBundleResource("all-vendors.js", "/WEB-INF/crm/web/js/vendors/vue-i18n-8.22.4.js");

        bundleResourceResolver.addBundleResource("all-app.js", "/WEB-INF/crm/web/js/errors.js");
        bundleResourceResolver.addBundleResource("all-app.js", "/WEB-INF/crm/web/js/views.js");
        bundleResourceResolver.addBundleResource("all-app.js", "/WEB-INF/crm/web/js/components.js");
        bundleResourceResolver.addBundleResource("all-app.js", "/WEB-INF/crm/web/js/features.js");
        bundleResourceResolver.addBundleResource("all-app.js", "/WEB-INF/crm/web/js/app.js");

        bundleResourceResolver.addBundleResource("all-app.css", "/WEB-INF/crm/web/css/main.css");

        bundleResourceResolver.primeCache();
        chain.addResolver(new VersionResourceResolver() //
                .addContentVersionStrategy("/**")) //
                .addResolver(bundleResourceResolver //
                );

    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2) //
                .select() //
                .apis(RequestHandlerSelectors.withClassAnnotation(SwaggerExpose.class)) //
                .paths(PathSelectors.any()) //
                .build();
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(generalHandlerExceptionResolver());
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository tokenRepository = new CookieCsrfTokenRepository();
        tokenRepository.setCookieHttpOnly(false);
        return tokenRepository;
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
        return filter;
    }

    @Bean
    public GeneralHandlerExceptionResolver generalHandlerExceptionResolver() {
        return new GeneralHandlerExceptionResolver();
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean
    public CookieLocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setCookieMaxAge(2 * 7 * 24 * 60 * 60);// 2 weeks
        cookieLocaleResolver.setCookieName("lang");
        return cookieLocaleResolver;
    }

    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        ResourceUrlEncodingFilter resourceUrlEncodingFilter = new ResourceUrlEncodingFilter();
        return resourceUrlEncodingFilter;
    }

}
