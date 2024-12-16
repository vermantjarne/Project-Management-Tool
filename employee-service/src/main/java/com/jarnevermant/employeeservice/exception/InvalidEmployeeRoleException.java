package com.jarnevermant.employeeservice.exception;

public class InvalidEmployeeRoleException extends RuntimeException {

    public InvalidEmployeeRoleException(String message) {
        super(message);
    }

}
