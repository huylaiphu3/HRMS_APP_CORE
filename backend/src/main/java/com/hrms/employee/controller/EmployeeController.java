package com.hrms.employee.controller;

import com.hrms.common.dto.ApiResponse;
import com.hrms.employee.dto.EmployeeDTO;
import com.hrms.employee.service.EmployeeService;
import com.hrms.employee.service.serviceImpl.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<EmployeeDTO>> save(@RequestBody EmployeeDTO employeeDTO){
        employeeService.save(employeeDTO);
        return ResponseEntity.ok(ApiResponse.success(200,employeeDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> findById(@PathVariable int id){
        EmployeeDTO employeeDTO = employeeService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200,employeeDTO));
    }

    @PutMapping("/update")
    public ResponseEntity<EmployeeDTO> update(@RequestBody EmployeeDTO employeeDTO){
        employeeService.update(employeeDTO);
        return ResponseEntity.ok(employeeDTO);
    }
    //tìm hiểu thêm về request param vs path variable
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable int id){
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200,"Đã xóa employee với id = " + id));
    }

    @PostMapping("/hello")
    private String hello(){
        return "Hello";
    }


}
