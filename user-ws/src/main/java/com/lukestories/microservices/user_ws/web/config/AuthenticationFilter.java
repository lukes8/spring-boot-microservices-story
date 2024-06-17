package com.lukestories.microservices.user_ws.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lukestories.microservices.user_ws.web.model.dto.LoginRequestModel;
import com.zaxxer.hikari.pool.HikariProxyCallableStatement;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.Base64;
import java.util.Date;



@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper;
    private final Environment environment;
    
    public AuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, Environment environment) {
        super(authenticationManager);
        this.setFilterProcessesUrl("/login");

        this.authenticationManager = authenticationManager;
        this.objectMapper = objectMapper;
        this.environment = environment;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            log.debug("Request URL: {}", request.getRequestURL());

            LoginRequestModel loginRequestModel = objectMapper.readValue(request.getInputStream(), LoginRequestModel.class);
            log.debug("User: {} Password: {}", loginRequestModel.getUsername(), loginRequestModel.getPassword());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequestModel.getUsername(), loginRequestModel.getPassword());

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            log.error("Error reading login request", e);
            throw new BadCredentialsException("Invalid login request");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // Set the authentication to prevent further processing
        SecurityContextHolder.getContext().setAuthentication(authResult);
        // Proceed with the chain
        chain.doFilter(request, response);
    }
}
//
//@Slf4j
//public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private Environment environment;
//
//    public AuthenticationFilter(AuthenticationManager authenticationManager, Environment environment) {
//        super(authenticationManager);
//        this.environment = environment;
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//
//        try {
//            log.debug("request url: {}", request.getRequestURL());
//            log.debug("request username: {}", request.getParameter("username"));
//            log.debug("request password: {}", request.getParameter("password"));
//            String username = request.getParameter("username");
//            String password = request.getParameter("password");
//            if (username != null && password != null) {
//                log.debug("form params username: {}, password: {} ", username, password);
//            }
//            LoginRequestModel loginRequestModel = new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);
//            log.debug("user: {} password: {}", loginRequestModel.getUsername(), loginRequestModel.getPassword());
//
//            AuthenticationManager authenticationManager = getAuthenticationManager();
//            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestModel.getUsername(), loginRequestModel.getPassword()));
//        } catch (IOException e) {
//            log.error("user not authenticated");
//            throw new BadCredentialsException("user not authenticated");
//        }
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {
//
//        String username = authResult.getName();
//        log.info("successfulAuthentication");
//
//        User principal = (User) authResult.getPrincipal();
//        String secret = environment.getProperty("token.secret");
//        log.info("secret: {}", secret);
//        long expirationDate = Long.parseLong(environment.getProperty("token.expiration_date"));
//        log.debug("tokenSecret: {}  expirationDate: {}", secret, expirationDate);
//        byte[] secretKeyBytes = Base64.getEncoder().encode(secret.getBytes());
//        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
//        Instant now = Instant.now();
//        String jsonWebToken = Jwts.builder().subject(principal.getUsername())
//                .expiration(Date.from(now.plusMillis(expirationDate)))
//                .issuedAt(Date.from(now))
//                .signWith(secretKey)
//                .compact();
//
//        response.addHeader("token", jsonWebToken);
//        response.addHeader("user", principal.getUsername());
//        log.debug("token: {} for user {}", jsonWebToken, principal.getUsername());
//
//        super.successfulAuthentication(request, response, chain, authResult);
//    }
//}
