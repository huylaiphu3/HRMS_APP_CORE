package com.hrms.employee.dto;

import com.hrms.employee.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public Employee convertToEntity(EmployeeDTO employeeDTO){
        return   Employee.builder()
                .name(employeeDTO.getName())
                .email(employeeDTO.getEmail())
                .phone(employeeDTO.getPhone())
                .address(employeeDTO.getAddress())
                .position(employeeDTO.getPosition())
                .build();
    }

    public EmployeeDTO convertToDTO(Employee employee){
        return EmployeeDTO.builder()
                .name(employee.getName()).email(employee.getEmail()).phone(employee.getPhone())
                .address(employee.getAddress()).position(employee.getPosition()).build();
    }
}
