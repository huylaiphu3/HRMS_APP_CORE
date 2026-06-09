package com.hrms.employee.service.serviceImpl;

import com.hrms.employee.dto.EmployeeDTO;
import com.hrms.employee.dto.EmployeeMapper;
import com.hrms.employee.entity.Employee;
import com.hrms.employee.repository.EmployeeRepository;
import com.hrms.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Override
    public void save(EmployeeDTO employeeDTO) {
        employeeRepository.save(employeeMapper.convertToEntity(employeeDTO));
    }

    @Override
    public void update(EmployeeDTO employeeDTO) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public EmployeeDTO findById(int id) {
        return null;
    }

    @Override
    public EmployeeDTO findByName(String name) {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
