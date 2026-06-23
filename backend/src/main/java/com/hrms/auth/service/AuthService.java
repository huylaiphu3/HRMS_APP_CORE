package com.hrms.auth.service;

import com.hrms.auth.dto.LoginRequest;
import com.hrms.auth.dto.LoginResponse;
import com.hrms.auth.dto.RefreshTokenResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    RefreshTokenResponse refresh(String refreshTokenValue);
}