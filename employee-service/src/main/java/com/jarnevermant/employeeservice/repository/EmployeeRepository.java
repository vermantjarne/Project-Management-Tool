package com.jarnevermant.employeeservice.repository;


import com.jarnevermant.employeeservice.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository extends MongoRepository<Employee, String> {



}
