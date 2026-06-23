//package com.hrms.user.service;
//
//import com.hrms.common.util.JwtUtil;
//import com.hrms.user.dto.LoginRequest;
//import com.hrms.user.dto.LoginResponse;
//import com.hrms.user.entity.User;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//    private final PasswordEncoder passwordEncoder;
//    private final CustomUserDetailService customUserDetailService;
//    private final JwtUtil jwtUtil;
//
//    public LoginResponse login(LoginRequest loginRequest) {
//        //load user
//        User user = (User) customUserDetailService.loadUserByUsername(loginRequest.getUsername());
//        //check pass
//        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
//            throw new RuntimeException("Sai mật khẩu");
//        }
//        //tạo token
//        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUserRole());
//        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
//        return LoginResponse.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }
//}
