package com.hrms.user.entity;

import com.hrms.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseEntity implements UserDetails {
    private String username;
    private String password;
    private String fullName;
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }
    private int failedLoginAttempts = 0;
    private LocalDateTime lockedUntil;


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
