package com.hrms.employee.service.serviceImpl;

import com.hrms.common.exception.ResourceNotFound;
import com.hrms.employee.dto.EmployeeDTO;
import com.hrms.employee.dto.EmployeeMapper;
import com.hrms.employee.entity.Employee;
import com.hrms.employee.repository.EmployeeRepository;
import com.hrms.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Override
    public void save(EmployeeDTO employeeDTO) {
        employeeRepository.save(employeeMapper.convertToEntity(employeeDTO)); // nó sẽ tự sinh ra câu sql chaỵy ở hệ thống
    /*
    tức là noó đang mapping entity của java vs table của db -> 1 kĩ thuật -> orm ( object related mapping )
    repository -> đang sử dụng jpa -> jpa sử dụng hibernate -> đẻ sinh ra câu sql -> sử dụng jdbc
     */
    }


    @Override
    public void update(EmployeeDTO employeeDTO) {
        // đầu tiên phải tìm dc thằng cần update
        // giả sử tên ko dc sửa thì sẽ tìm theo tên
        Employee employee = employeeRepository.findByName(employeeDTO.getName());
        //hàm save
        employee.setEmail(employeeDTO.getEmail());
        employee.setPhone(employeeDTO.getPhone());
        employee.setAddress(employeeDTO.getAddress());
        employee.setPosition(employeeDTO.getPosition());
        employeeRepository.save(employee);
    }

    @Override
    public void delete(int id) {
        //hàm delete
    }

    @Override
    public EmployeeDTO findById(int id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if(employee == null)
            throw new ResourceNotFound("Employee not found","Ha noi");
        return employeeMapper.convertToDTO(employee);
    }

    @Override
    public EmployeeDTO findByName(String name) {
        return null;
    }

    @Override
    public void deleteAll() {

    }
}
