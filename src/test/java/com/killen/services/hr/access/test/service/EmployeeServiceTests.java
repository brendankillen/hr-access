package com.killen.services.hr.access.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.killen.services.hr.access.HrAccess;
import com.killen.services.hr.access.entity.Department;
import com.killen.services.hr.access.entity.Employee;
import com.killen.services.hr.access.entity.Finance;
import com.killen.services.hr.access.entity.Role;
import com.killen.services.hr.access.exception.EmployeeByUserNameException;
import com.killen.services.hr.access.exception.EmployeeCreateException;
import com.killen.services.hr.access.exception.EmployeeDeleteException;
import com.killen.services.hr.access.exception.EmployeeUpdateException;
import com.killen.services.hr.access.repository.IDepartmentRepository;
import com.killen.services.hr.access.repository.IEmployeeRepository;
import com.killen.services.hr.access.repository.IRoleRepository;
import com.killen.services.hr.access.service.EmployeeService;
import com.killen.services.hr.access.test.EmployeeTestData;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HrAccess.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class EmployeeServiceTests
{
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private IEmployeeRepository employeeRepository;
	
	@Autowired
	private IDepartmentRepository departmentRepository;
	
	@Autowired
	private IRoleRepository roleRepository;
	
	private EmployeeTestData testData;
	
	@Before
	public void init() throws EmployeeCreateException
	{
		testData = new EmployeeTestData(employeeService, 
										employeeRepository,
										departmentRepository,
										roleRepository);
	}
	
	@After
	public void cleanUp() throws EmployeeDeleteException
	{
		//testData.deleteTestData();
	}
	
	@Test
	public void testCreateNewEmployee()
	{
		assertTrue(employeeService.getEnabledEmployees().size() == 7);
	}
	
	@Test(expected = EmployeeCreateException.class)
	public void testCreateNewEmployeeNullRole() throws EmployeeCreateException
	{
		employeeService.createNewEmployee(new Employee(0, 
													   "Jen", 
													   "Turner", 
													   -1, 
													   null, 
													   new ArrayList<Finance>(), 
													   null, 
													   new Department(0, "HR"), 
													   25, 
													   33000, 
													   2000));
	}
	
	@Test(expected = EmployeeCreateException.class)
	public void testCreateNewEmployeeNullDepartment() throws EmployeeCreateException
	{
		employeeService.createNewEmployee(new Employee(0, 
													   "Jen", 
													   "Turner", 
													   -1, 
													   new Role(0, EmployeeService.ROLE_MANAGER), 
													   new ArrayList<Finance>(), 
													   null, 
													   null, 
													   25, 
													   33000, 
													   2000));
	}
	
	@Test(expected = EmployeeByUserNameException.class)
	public void testDeleteEmployee() throws EmployeeDeleteException,
											EmployeeByUserNameException, 
											EmployeeCreateException
	{
		Employee tempEmployee = employeeService.createNewEmployee(new Employee(0, 
				   															   "Jen", 
																			   "Turner", 
																			   -1, 
																			   new Role(0, EmployeeService.ROLE_MANAGER), 
																			   new ArrayList<Finance>(), 
																			   null, 
																			   new Department(0, "HR"), 
																			   25, 
																			   33000, 
																			   2000));
		
		String userName = tempEmployee.getUser().getUsername();
		
		employeeService.deleteEmployee(tempEmployee);
		
		employeeService.getEmployeeByUserName(userName);
	}
	
	@Test
	public void testEmployeeDeleteReferencedManager() throws EmployeeCreateException, EmployeeDeleteException,
															 EmployeeByUserNameException
	{
		employeeService.deleteEmployee(testData.getHrManager());
		
		Employee employee = employeeService.getEmployeeByUserName(testData.getHrEmployeeList().get(0).getUser().getUsername());
		
		assertTrue(employee.getManagerId() == EmployeeService.MANAGER_DEFAULT_ID);
	}
	
	@Test
	public void testEmployeeByUserName() throws EmployeeByUserNameException
	{
		Employee tempEmployee = employeeService.getEmployeeByUserName(testData.getHrManager().getUser().getUsername());
		
		assertEquals(testData.getHrManager().getId(), tempEmployee.getId());
		assertEquals(testData.getHrManager().getRole().getId(), tempEmployee.getRole().getId());
		assertEquals(testData.getHrManager().getDepartment().getId(), tempEmployee.getDepartment().getId());
		assertEquals(testData.getHrManager().getUser().getId(), tempEmployee.getUser().getId());
	}
	
	@Test(expected = EmployeeByUserNameException.class)
	public void testEmployeeByUserNameEmptyString() throws EmployeeByUserNameException
	{
		employeeService.getEmployeeByUserName("");
	}
	
	@Test(expected = EmployeeByUserNameException.class)
	public void testEmployeeByUserNameNull() throws EmployeeByUserNameException
	{
		employeeService.getEmployeeByUserName(null);
	}
		
	@Test(expected = EmployeeUpdateException.class)
	public void testUpdateEmployeeNull() throws EmployeeUpdateException
	{
		employeeService.updateEmployee(null);
	}
	
	@Test(expected = EmployeeUpdateException.class)
	public void testUpdateEmployeeInvalidName() throws EmployeeUpdateException
	{
		testData.getHrEmployeeList().get(0).setFirstName("");
		testData.getHrEmployeeList().get(0).setLastName(null);
		
		employeeService.updateEmployee(testData.getHrEmployeeList().get(0));
	}
	
	@Test
	public void testUpdateEmployeeUserUpdated() throws EmployeeUpdateException
	{
		String updateFirstName = "John";
		String updateLastName = "Doe";
		String testUserName = "jdoe";
		
		testData.getHrEmployeeList().get(0).setFirstName(updateFirstName);
		testData.getHrEmployeeList().get(0).setLastName(updateLastName);
		
		Employee updatedEmployee = employeeService.updateEmployee(testData.getHrEmployeeList().get(0));
		
		assertEquals(testUserName, updatedEmployee.getUser().getUsername());
	}
	
	@Test 
	public void testUpdatedEmployeeSalaryHistoryCreated() throws EmployeeUpdateException
	{
		double updatedSalary = 35000;
		double oldSalary = testData.getHrEmployeeList().get(0).getSalary();
		testData.getHrEmployeeList().get(0).setSalary(updatedSalary);
		
		Employee updatedEmployee = employeeService.updateEmployee(testData.getHrEmployeeList().get(0));
		
		assertTrue(updatedEmployee.getFinances().size() == 1);
		assertTrue(updatedEmployee.getFinances().get(0).getType().equals(EmployeeService.FINANCE_SALARY));
		assertTrue(updatedEmployee.getFinances().get(0).getOldValue() == oldSalary);
		assertTrue(updatedEmployee.getFinances().get(0).getNewValue() == updatedSalary);
	}
	
	@Test 
	public void testUpdatedEmployeeBonusHistoryCreated() throws EmployeeUpdateException
	{
		double updatedBonus = 2100;
		double oldBonus = testData.getHrEmployeeList().get(0).getBonus();
		testData.getHrEmployeeList().get(0).setBonus(updatedBonus);
		
		Employee updatedEmployee = employeeService.updateEmployee(testData.getHrEmployeeList().get(0));
		
		assertTrue(updatedEmployee.getFinances().size() == 1);
		assertTrue(updatedEmployee.getFinances().get(0).getType().equals(EmployeeService.FINANCE_BONUS));
		assertTrue(updatedEmployee.getFinances().get(0).getOldValue() == oldBonus);
		assertTrue(updatedEmployee.getFinances().get(0).getNewValue() == updatedBonus);
	}
	
	@Test
	public void testGetAllEmployeesByManagerId()
	{
		assertEquals(2, employeeService.getAllEmployeesByManagerId(testData.getHrManager().getId()).size());
		assertEquals(2, employeeService.getAllEmployeesByManagerId(testData.getHrManager().getId()).size());
	}
	
	@Test
	public void testGetAllEmployeesByManagerIdDefault()
	{
		assertEquals(3, employeeService.getAllEmployeesByManagerId(EmployeeService.MANAGER_DEFAULT_ID).size());
	}
	
	@Test
	public void testGetAllEmployeesByManagerIdInvalid()
	{
		assertTrue(employeeService.getAllEmployeesByManagerId(99999999).isEmpty());
	}
	
	@Test
	public void testGetOnlyManagers()
	{
		List<Employee> managers = employeeService.getAllManagers().stream()
																  .filter(e ->  e.getRole().getType().equals(EmployeeService.ROLE_MANAGER))
																  .collect(Collectors.toList());
		
		assertTrue(managers.size() > 0);
	}
	
	@Test
	public void testGetAllEmployeesNotInHrDepartment()
	{
		assertTrue(employeeService.getAllEmployeesNotInHrDepartment().stream()
														  			 .filter(e ->  e.getDepartment().getName().equals(EmployeeService.DEPARTMENT_HR))
														  			 .collect(Collectors.toList()).isEmpty());
	}
}
