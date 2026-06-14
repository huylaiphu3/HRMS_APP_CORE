package com.hrms.common.exception;

import com.hrms.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(ResourceNotFound.class)
    private ResponseEntity<ApiResponse<String>> handleResourceNotFound(ResourceNotFound e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404,e.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    private ResponseEntity<ApiResponse<String>> handleUsernameNotFoundException(UsernameNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(404,e.getMessage()));
    }
}
