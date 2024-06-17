package com.lukestories.microservices.user_ws.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lukestories.microservices.user_ws.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private Environment environment;
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder encoder;

//    @Autowired
//    private CustomLoggingFilter customLoggingFilter;

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    @ConditionalOnProperty(name = "spring-security-enabled", havingValue = "false", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain4NonSecureEnv(HttpSecurity http) throws Exception {

        log.info("non secure env");
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }


    @Bean
    @ConditionalOnProperty(name = "spring-security-enabled", havingValue = "true", matchIfMissing = false)
    public SecurityFilterChain securityFilterChain4SecureEnv(HttpSecurity http) throws Exception {
        log.info("secure env");

        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(encoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        AuthenticationFilter authFilter = new AuthenticationFilter(authenticationManager, objectMapper, environment);
        authFilter.setFilterProcessesUrl("/login");
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(environment);

        log.info("User name and password: {} : {}", environment.getProperty("test.user.username"), environment.getProperty("test.user.password"));
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);
        http.headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        http.authorizeHttpRequests(
                        auth -> auth.requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/rest/api/jwt/**").permitAll())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        http.formLogin(Customizer.withDefaults());

        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationManager(authenticationManager)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http, UserService userService, BCryptPasswordEncoder encoder) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(encoder);
//        return authenticationManagerBuilder.build();
//
//    }

//    @Bean
//    public AuthenticationFilter authenticationFilter() {
//        AuthenticationFilter filter = new AuthenticationFilter(authenticationManager, objectMapper, environment);
//        filter.setFilterProcessesUrl("/login");
//        return filter;
//    }
}
