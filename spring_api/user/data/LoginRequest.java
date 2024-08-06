package com.lab2ai.cosign.user.data;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
