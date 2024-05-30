package com.lukestories.microservices.user_ws.web.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder @NoArgsConstructor @AllArgsConstructor @Data
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private Boolean active;
    private LocalDateTime lastLoginDateTime;
}
