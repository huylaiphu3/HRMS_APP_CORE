package com.hrms.auth.service.serviceImpl;

import com.hrms.auth.dto.LoginRequest;
import com.hrms.auth.dto.LoginResponse;
import com.hrms.auth.dto.RefreshTokenResponse;
import com.hrms.auth.entity.RefreshToken;
import com.hrms.auth.repository.RefreshTokenRepository;
import com.hrms.auth.service.AuthService;
import com.hrms.common.util.JwtUtil;
import com.hrms.user.entity.User;
import com.hrms.user.entity.UserStatus;
import com.hrms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy username"));

        if (user.getUserStatus() == UserStatus.LOCKED &&
            user.getLockedUntil() != null &&
            user.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Tài khoản đang bị khóa");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= 5) {
                user.setUserStatus(UserStatus.LOCKED);
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
            }
            userRepository.save(user);
            throw new RuntimeException("Sai mật khẩu");
        }

        user.setFailedLoginAttempts(0);
        user.setUserStatus(UserStatus.ACTIVE);
        user.setLockedUntil(null);
        userRepository.save(user);

        String accessTokenValue = jwtUtil.generateAccessToken(user.getId(), user.getUserRole());
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .revoked(false).build();
        refreshTokenRepository.save(refreshToken);
        return new LoginResponse(
                accessTokenValue,
                refreshTokenValue
        );
    }

    @Override
    public RefreshTokenResponse refresh(String refreshTokenValue) {
        RefreshToken token = refreshTokenRepository.findByTokenAndRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh Token không hợp lệ"));
        if (token.getRevoked()){
            throw new RuntimeException("Token bị revoked");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Token hết hạn");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getUserRole()
        );
        return new RefreshTokenResponse(newAccessToken);
    }
}
