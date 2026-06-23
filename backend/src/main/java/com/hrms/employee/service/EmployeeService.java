package com.hrms.employee.service;

import com.hrms.employee.dto.EmployeeDTO;


public interface EmployeeService {
    void save(EmployeeDTO employeeDTO);
    void update(EmployeeDTO employeeDTO);
    void delete(int id);
    EmployeeDTO findById(int id);
    EmployeeDTO findByName(String name);
    void deleteAll();
}
