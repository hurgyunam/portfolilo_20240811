package com.lab2ai.cosign.user.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@AllArgsConstructor
public class AuthenticationToken {
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    private String token;
}
