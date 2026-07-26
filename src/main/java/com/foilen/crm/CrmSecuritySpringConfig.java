package com.foilen.crm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@Profile("!JUNIT")
public class CrmSecuritySpringConfig {

    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf
                .csrfTokenRepository(cookieCsrfTokenRepository())
                .csrfTokenRequestHandler((request, response, supplier) -> {
                    String token = request.getHeader("X-XSRF-TOKEN");
                    if (token != null) {
                        request.setAttribute("_csrf", token);
                    }
                }));

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/").permitAll()
                .requestMatchers("/index.html").permitAll()
                .requestMatchers("/assets/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/api/csrf").permitAll()
                .requestMatchers("/api/app/details").permitAll()
                .requestMatchers("/api/user/login").permitAll()
                .requestMatchers("/api/user/loginWithCodeRequest").permitAll()
                .requestMatchers("/api/user/loginWithCode").permitAll()
                .anyRequest().authenticated());

        return http.build();
    }

}
