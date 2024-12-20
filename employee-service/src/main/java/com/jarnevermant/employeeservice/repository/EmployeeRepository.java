package com.jarnevermant.employeeservice.repository;


import com.jarnevermant.employeeservice.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    boolean existsByEmployeeIdentifier(String employeeIdentifier);
    Optional<Employee> findByEmployeeIdentifier(String employeeIdentifier);
    void deleteByEmployeeIdentifier(String employeeIdentifier);

}
