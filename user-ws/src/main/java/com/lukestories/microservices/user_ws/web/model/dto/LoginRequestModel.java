package com.lukestories.microservices.user_ws.web.model.dto;

import lombok.Data;

@Data
public class LoginRequestModel {
    private String username;
    private String password;
}
