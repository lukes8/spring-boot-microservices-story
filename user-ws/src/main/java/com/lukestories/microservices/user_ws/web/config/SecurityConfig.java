package com.lukestories.microservices.user_ws.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private Environment environment;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("User name and password: {} : {}", environment.getProperty("test.user.username"), environment.getProperty("test.user.password"));
        http.httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(
                        r -> r
//                                .anyRequest().permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().authenticated()
                );
        http.csrf(c -> c.disable())
                .headers(c -> c.frameOptions(f -> f.disable()));
        http.sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.withUserDetails(new UserDetails() {
            List<GrantedAuthority> lst = AuthorityUtils.createAuthorityList("USER");

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return lst;
            }

            @Override
            public String getPassword() {
                return new BCryptPasswordEncoder().encode("test");
            }

            @Override
            public String getUsername() {
                return "test";
            }
        }).build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
