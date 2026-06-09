package com.hrms.employee.service.serviceImpl;

import com.hrms.employee.dto.EmployeeDTO;
import com.hrms.employee.dto.EmployeeMapper;
import com.hrms.employee.repository.EmployeeRepository;
import com.hrms.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary// primary annotation is used to mark the bean as primary bean ( bean ưu tiên ) = qualifier annotation = @Qualifier("employeeServiceImplV2") = @Order
public class EmployeeServiceImplV2 implements EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImplV2.class);
    @Autowired
    private EmployeeRepository employeeRepository;

    // tạo bean qua constructor
    //tạo bean qua setter
    // có 3 cách inject : constructor , setter , autowired
    // tạo bean có 3 cách : @Component , @Configuration , xml
    @Autowired
    private EmployeeMapper employeeMapper;
    @Override
    public void save(EmployeeDTO employeeDTO) {
        log.info("this is save method --version 2");
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
