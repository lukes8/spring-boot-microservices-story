package com.lukestories.microservices.user_ws.web.config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Base64;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final Environment environment;
    private final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    public JwtAuthorizationFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {

        if (request.getHeader("Authorization") == null) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String jwtToken = authorizationHeader.replace("Bearer ", "");
        if (!validateToken(jwtToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateToken(String jwtToken) {

        String secretVal = environment.getProperty("token.secret");
        byte[] secretBytes = Base64.getEncoder().encode(secretVal.getBytes());
        logger.info("secret: {}", secretVal);
        SecretKey secretKey = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS512.getJcaName());

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        try {
            Jws<Claims> claimsJws = jwtParser.parseSignedClaims(jwtToken);
            String subject = claimsJws.getPayload().getSubject();
            logger.info("JWT token auth successful");
            logger.info("JWT token claims[subject]: {}", subject);
            Jwt<?, ?> parse = jwtParser.parse(jwtToken);
        } catch (ExpiredJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            logger.error("JWT token verification failed: {}", e.getMessage());
            return false;
        }
        return true;
    }
}
