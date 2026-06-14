package com.hrms.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  ApiResponse<T> {
    private T data;
    private int code;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(int code ,T data){
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
    public static <T> ApiResponse<T> error(int code,T data){
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}
