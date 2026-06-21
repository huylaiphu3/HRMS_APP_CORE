package com.hrms.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final Long userId;
    private final String role;
    private final Long departmentId;

    public CustomUserDetails(Long userId, String role, Long departmentId,
                             String username, String password,
                             Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userId = userId;
        this.role = role;
        this.departmentId = departmentId;
    }
}
