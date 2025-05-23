package com.foilen.crm;

import com.foilen.crm.web.controller.GeneralHandlerExceptionResolver;
import com.foilen.crm.web.controller.SwaggerExpose;
import com.foilen.crm.web.interceptor.ProcessUserInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
@EnableOpenApi
@ComponentScan({"com.foilen.crm.web"})
public class CrmWebSpringConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(processUserInterceptor());
        registry.addInterceptor(localeChangeInterceptor());
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
    public ProcessUserInterceptor processUserInterceptor() {
        return new ProcessUserInterceptor();
    }

    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        ResourceUrlEncodingFilter resourceUrlEncodingFilter = new ResourceUrlEncodingFilter();
        return resourceUrlEncodingFilter;
    }

}
