package com.jarnevermant.employeeservice;

import com.jarnevermant.employeeservice.dto.EmployeeRequest;
import com.jarnevermant.employeeservice.dto.EmployeeResponse;
import com.jarnevermant.employeeservice.exception.EmployeeNotFoundException;
import com.jarnevermant.employeeservice.model.Employee;
import com.jarnevermant.employeeservice.repository.EmployeeRepository;
import com.jarnevermant.employeeservice.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceApplicationTests {

	@InjectMocks
	private EmployeeService employeeService;

	@Mock
	private EmployeeRepository employeeRepository;

	@Test
	public void testAddEmployee() {
		// Arrange
		EmployeeRequest employeeRequest = new EmployeeRequest();
		employeeRequest.setFirstName("Bertha");
		employeeRequest.setLastName("Powells");
		employeeRequest.setRole("Developer");
		employeeRequest.setStartDate(LocalDate.of(2024,12,28));

		// Act
		employeeService.addEmployee(employeeRequest);

		// Assert
		verify(employeeRepository, times(1)).save(any(Employee.class));
	}

	@Test
	public void testGetAllEmployees() {
		// Arrange
		Employee employee = new Employee();
		employee.setEmployeeIdentifier("EMP-123");
		employee.setFirstName("Bertha");
		employee.setLastName("Powells");
		employee.setRole("Developer");
		employee.setStartDate(LocalDate.of(2024, 12, 28));

		when(employeeRepository.findAll()).thenReturn(List.of(employee));

		// Act
		List<EmployeeResponse> employees = employeeService.getAllEmployees();

		// Assert
		assertEquals(1, employees.size());
		assertEquals("EMP-123", employees.get(0).getEmployeeIdentifier());
		assertEquals("Bertha", employees.get(0).getFirstName());
		assertEquals("Powells", employees.get(0).getLastName());
		assertEquals("Developer", employees.get(0).getRole());
		assertEquals(LocalDate.of(2024, 12, 28), employees.get(0).getStartDate());

		verify(employeeRepository, times(1)).findAll();
	}

	@Test
	public void testGetEmployee_Success() {
		// Arrange
		String employeeIdentifier = "EMP-123";

		Employee employee = new Employee();
		employee.setEmployeeIdentifier(employeeIdentifier);
		employee.setFirstName("Bertha");
		employee.setLastName("Powells");
		employee.setRole("Developer");
		employee.setStartDate(LocalDate.of(2024, 12, 28));

		when(employeeRepository.findByEmployeeIdentifier(employeeIdentifier)).thenReturn(Optional.of(employee));

		// Act
		EmployeeResponse employeeResponse = employeeService.getEmployee(employeeIdentifier);

		// Assert
		assertEquals("EMP-123", employeeResponse.getEmployeeIdentifier());
		assertEquals("Bertha", employeeResponse.getFirstName());
		assertEquals("Powells", employeeResponse.getLastName());
		assertEquals("Developer", employeeResponse.getRole());
		assertEquals(LocalDate.of(2024, 12, 28), employeeResponse.getStartDate());

		verify(employeeRepository, times(1)).findByEmployeeIdentifier(employeeIdentifier);
	}

	@Test
	public void testGetEmployee_EmployeeNotFound() {
		// Arrange
		String employeeIdentifier = "EMP-123";

		when(employeeRepository.findByEmployeeIdentifier(employeeIdentifier)).thenReturn(Optional.empty());

		// Act & Assert
		EmployeeNotFoundException thrown = assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.getEmployee(employeeIdentifier);
		});

		assertEquals("The employee does not exist", thrown.getMessage());

		verify(employeeRepository, times(1)).findByEmployeeIdentifier(employeeIdentifier);
	}

	@Test
	public void testUpdateEmployee_Success() {
		// Arrange
		String employeeIdentifier = "EMP-123";

		Employee employee = new Employee();
		employee.setEmployeeIdentifier(employeeIdentifier);
		employee.setFirstName("Bertha");
		employee.setLastName("Powells");
		employee.setRole("Developer");
		employee.setStartDate(LocalDate.of(2024, 12, 28));

		EmployeeRequest employeeRequest = new EmployeeRequest();
		employeeRequest.setRole("Project Lead");

		when(employeeRepository.findByEmployeeIdentifier(employeeIdentifier)).thenReturn(Optional.of(employee));

		// Act
		EmployeeResponse updatedEmployee = employeeService.updateEmployee(employeeIdentifier, employeeRequest);

		// Assert
		assertEquals("Project Lead", updatedEmployee.getRole());

		verify(employeeRepository, times(1)).save(employee);
	}

	@Test
	public void testUpdateEmployee_EmployeeNotFound() {
		// Arrange
		String employeeIdentifier = "EMP-123";

		EmployeeRequest employeeRequest = new EmployeeRequest();
		employeeRequest.setRole("Project Lead");

		when(employeeRepository.findByEmployeeIdentifier(employeeIdentifier)).thenReturn(Optional.empty());

		// Act & Assert
		EmployeeNotFoundException thrown = assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.updateEmployee(employeeIdentifier, employeeRequest);
		});

		assertEquals("The employee does not exist", thrown.getMessage());

		verify(employeeRepository, times(0)).save(any());
	}

	@Test
	public void testDeleteEmployee_Success() {
		// Arrange
		String employeeIdentifier = "EMP-123";

		when(employeeRepository.existsByEmployeeIdentifier(employeeIdentifier)).thenReturn(true);

		// Act
		employeeService.deleteEmployee(employeeIdentifier);

		// Assert
		verify(employeeRepository, times(1)).deleteByEmployeeIdentifier(employeeIdentifier);
	}

	@Test
	public void testDeleteEmployee_EmployeeNotFound() {
		// Arrange
		String employeeIdentifier = "EMP-123";

		when(employeeRepository.existsByEmployeeIdentifier(employeeIdentifier)).thenReturn(false);

		// Act & Assert
		EmployeeNotFoundException thrown = assertThrows(EmployeeNotFoundException.class, () -> {
			employeeService.deleteEmployee(employeeIdentifier);
		});

		assertEquals("The employee does not exist", thrown.getMessage());

		verify(employeeRepository, times(0)).deleteByEmployeeIdentifier(anyString());
	}

}
