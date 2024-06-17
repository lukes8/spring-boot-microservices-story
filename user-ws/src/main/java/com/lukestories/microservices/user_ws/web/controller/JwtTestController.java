package com.lukestories.microservices.user_ws.web.controller;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Date;

@RestController
@RequestMapping("/rest/api/jwt-token") @Slf4j
public class JwtTestController {

    @Value("${token.secret:SECRET_IS_NOT_SET}")
    private String secret;

    @GetMapping("/generate/{username}")
    public String generateToken(@PathVariable String username) {

        log.info("secret: " + secret);
        byte[] keyBytes = secret.getBytes();
        SecretKey secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());

        Instant now = Instant.now();

        String jwtToken;
        try {
            jwtToken = Jwts.builder().signWith(secretKey).subject(username).issuedAt(Date.from(now)).expiration(
                    Date.from(now.plus(1, ChronoUnit.MINUTES))).compact();
        } catch (Exception e) {
            log.info("Exception thrown: " + e.getMessage());
            return "ex: " + e.getMessage();
        }

        return jwtToken;
    }

    @GetMapping("/check")
    public String checkJwtToken(HttpServletRequest request, HttpServletResponse response) {
        String authorization = request.getHeader("Authorization");
        String token = authorization.replace("Bearer ", "");

        byte[] keyBytes = secret.getBytes();
        SecretKey secretKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();
        try {
            Jws<Claims> claimsJws = jwtParser.parseSignedClaims(token);
            log.info("Auth JWT token success for {}", claimsJws.getPayload().getSubject());

        } catch (Exception e) {
            return e.getMessage();
        }

        return "success";
    }
}
