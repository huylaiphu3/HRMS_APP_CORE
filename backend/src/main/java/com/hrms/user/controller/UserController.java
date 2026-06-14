package com.hrms.user.controller;

import com.hrms.common.dto.ApiResponse;
import com.hrms.user.dto.LoginRequest;
import com.hrms.user.entity.User;
import com.hrms.user.service.CustomUserDetailService;
import com.hrms.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest){
        User u = (User) customUserDetailService.loadUserByUsername(loginRequest.getUsername());
        if (loginRequest.getPassword().equals(u.getPassword())){
            return ResponseEntity.ok(ApiResponse.success(200,"success"));
        } else {
            return ResponseEntity.ok(ApiResponse.error(400,"fail"));
        }
    }
}
