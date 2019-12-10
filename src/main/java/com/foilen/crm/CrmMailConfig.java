/*
    Foilen CRM
    https://github.com/foilen/foilen-crm
    Copyright (c) 2015-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.crm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.foilen.smalltools.email.EmailService;
import com.foilen.smalltools.email.EmailServiceSpring;

@Configuration
public class CrmMailConfig {

    @Value("${crm.mailHost}")
    private String emailHost;

    @Value("${crm.mailPort}")
    private int emailPort;

    @Value("${crm.mailUsername:#{null}}")
    private String emailUsername;

    @Value("${crm.mailPassword:#{null}}")
    private String emailPassword;

    @Bean
    public EmailService emailService() {
        return new EmailServiceSpring();
    }

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailHost);
        mailSender.setPort(emailPort);
        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);
        return mailSender;
    }

}
