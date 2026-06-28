package com.hrms.auth.service.serviceImpl;

import com.hrms.auth.dto.LoginRequest;
import com.hrms.auth.dto.LoginResponse;
import com.hrms.auth.entity.RefreshToken;
import com.hrms.auth.repository.RefreshTokenRepository;
import com.hrms.auth.service.AuthService;
import com.hrms.common.exception.BusinessException;
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
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(401, "Email hoặc mật khẩu không đúng"));

        // 1. Check khóa tài khoản
        if (user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil())) {
            throw new BusinessException(403, "Tài khoản đã bị khoá. Thử lại sau 15 phút");
        }

        // 2. Auto-unlock nếu hết thời gian khóa
        if (user.getLockedUntil() != null && LocalDateTime.now().isAfter(user.getLockedUntil())) {
            user.setLockedUntil(null);
            user.setFailedLoginAttempts(0);
            user.setUserStatus(UserStatus.ACTIVE);
        }

        // 3. Validate active và status
        if (!user.getActive() || user.getUserStatus() == UserStatus.INACTIVE) {
            throw new BusinessException(403, "Tài khoản đã bị vô hiệu hóa");
        }

        // 4. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
                user.setUserStatus(UserStatus.LOCKED);
            }
            userRepository.save(user);
            throw new BusinessException(401, "Email hoặc mật khẩu không đúng");
        }

        // 5. Reset failed attempts on success
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        if (user.getUserStatus() == UserStatus.LOCKED) user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        // 6. Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUserRole());
        String refreshTokenValue = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUserId(user.getId());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(accessToken, refreshTokenValue);
    }

    @Override
    public LoginResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(refreshTokenValue)
                .orElseThrow(() -> new BusinessException(401, "Phiên đăng nhập hết hạn"));

        if (LocalDateTime.now().isAfter(refreshToken.getExpiresAt())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new BusinessException(401, "Phiên đăng nhập hết hạn");
        }

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new BusinessException(401, "Phiên đăng nhập hết hạn"));

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getUserRole());
        String newRefreshTokenValue = UUID.randomUUID().toString();

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken(newRefreshTokenValue);
        newRefreshToken.setUserId(user.getId());
        newRefreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        newRefreshToken.setRevoked(false);
        refreshTokenRepository.save(newRefreshToken);

        return new LoginResponse(newAccessToken, newRefreshTokenValue);
    }
}
