package com.lab2ai.cosign.user.data;

import lombok.Data;

@Data
public class SignUpRequest {
    private String username;
    private String password;
    private String nickName;
}
