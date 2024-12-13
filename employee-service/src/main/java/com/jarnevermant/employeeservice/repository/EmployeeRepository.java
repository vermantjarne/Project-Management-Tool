package com.jarnevermant.employeeservice.repository;


import com.jarnevermant.employeeservice.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee, String> {

    List<Employee> findByEmployeeIdentifierIn(List<String> employeeIdentifier);

}
