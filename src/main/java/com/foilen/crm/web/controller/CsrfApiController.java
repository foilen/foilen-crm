package com.foilen.crm.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "api/csrf", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RestController
@SwaggerExpose
public class CsrfApiController {

    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    @GetMapping
    public ResponseEntity<Void> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken token = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(token, request, response);
        return ResponseEntity.ok().build();
    }
}
