//package com.hrms.user.controller;
//
//import com.hrms.common.dto.ApiResponse;
//import com.hrms.user.dto.LoginRequest;
//import com.hrms.user.dto.LoginResponse;
//import com.hrms.user.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1")
//@RequiredArgsConstructor
//public class UserController {
//    private final AuthService authService;
//    @PostMapping("auth/login")
//    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest){
//        return ResponseEntity.ok(
//                ApiResponse.success("Thành công", authService.login(loginRequest))
//        );
//    }
//}
