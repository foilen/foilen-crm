package com.foilen.crm;

import com.foilen.smalltools.email.EmailService;
import com.foilen.smalltools.email.EmailServiceSpring;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

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

    @Value("${crm.mailStartTlsEnable:false}")
    private boolean startTlsEnable;

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

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.starttls.enable", String.valueOf(startTlsEnable));
        props.put("mail.smtp.starttls.required", String.valueOf(startTlsEnable));

        // Only enable auth if username is provided
        if (emailUsername != null && !emailUsername.isEmpty()) {
            props.put("mail.smtp.auth", "true");
        }

        return mailSender;
    }

}
