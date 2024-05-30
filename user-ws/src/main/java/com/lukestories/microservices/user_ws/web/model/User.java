package com.lukestories.microservices.user_ws.web.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "USERS") @Builder @NoArgsConstructor @AllArgsConstructor @Data @EqualsAndHashCode @ToString
public class User {
    @Id @Column(name = "ID")
    private Long id;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "ENCRYPTED_PASSWORD")
    private String encryptedPassword;
    @Column(name = "ACTIVE")
    private Boolean active;
    @Column(name = "LAST_LOGIN_DATE_TIME")
    private LocalDateTime lastLoginDateTime;
}
