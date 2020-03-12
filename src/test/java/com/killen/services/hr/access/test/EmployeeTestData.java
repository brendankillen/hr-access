package com.killen.services.hr.access.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.killen.services.hr.access.entity.Department;
import com.killen.services.hr.access.entity.Employee;
import com.killen.services.hr.access.entity.Finance;
import com.killen.services.hr.access.entity.Role;
import com.killen.services.hr.access.exception.EmployeeCreateException;
import com.killen.services.hr.access.exception.EmployeeDeleteException;
import com.killen.services.hr.access.repository.DepartmentRepository;
import com.killen.services.hr.access.repository.EmployeeRepository;
import com.killen.services.hr.access.repository.RoleRepository;
import com.killen.services.hr.access.service.EmployeeService;


public class EmployeeTestData 
{
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	private Employee hrManager = null;
	private Employee itManager = null;
	
	private Department hrDepartment = null;
	private Department itDepartment = null;
	
	private Role managerRole = null;
	private Role employeeRole = null;
	
	List<Employee> hrEmployeeList = new ArrayList<>();
	List<Employee> itEmployeeList = new ArrayList<>();
	
	public EmployeeTestData(EmployeeService employeeService,
							EmployeeRepository employeeRepository,
							DepartmentRepository departmentRepository,
							RoleRepository roleRepository) throws EmployeeCreateException
	{
		this.employeeService = employeeService;
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
		this.roleRepository = roleRepository;
		
		createTestData();
	}
	
	public Employee getHrManager()
	{
		return hrManager;
	}
	
	public Employee getItManager()
	{
		return itManager;
	}
	
	public List<Employee> getHrEmployeeList()
	{
		return hrEmployeeList;
	}
	
	public List<Employee> getEmployeeList()
	{
		return itEmployeeList;
	}
	
	private void createTestData() throws EmployeeCreateException
	{
		hrDepartment = departmentRepository.save(new Department(1, EmployeeService.DEPARTMENT_HR));
		itDepartment = departmentRepository.save(new Department(2, EmployeeService.DEPARTMENT_IT));
		
		managerRole = roleRepository.save(new Role(1, EmployeeService.ROLE_MANAGER));
		employeeRole = roleRepository.save(new Role(2, EmployeeService.ROLE_EMPLOYEE));
		
		hrManager = new Employee(0, "Steve", "Turns", EmployeeService.MANAGER_DEFAULT_ID, new Role(0, EmployeeService.ROLE_MANAGER), new ArrayList<Finance>(), null, new Department(0, "HR"), 25, 33000, 2000);
		itManager = new Employee(0, "Eve", "Ray", EmployeeService.MANAGER_DEFAULT_ID, new Role(0, EmployeeService.ROLE_MANAGER), new ArrayList<Finance>(), null, new Department(0, "IT"), 25, 33000, 2000);
		
		employeeService.createNewEmployee(hrManager);
		employeeService.createNewEmployee(itManager);
		
		hrEmployeeList.add(employeeService.createNewEmployee(new Employee(0, "Jill", "Doherty", hrManager.getId(), new Role(0, EmployeeService.ROLE_EMPLOYEE), new ArrayList<Finance>(), null, new Department(0, EmployeeService.DEPARTMENT_HR), 25, 33000, 2000)));
		hrEmployeeList.add(employeeService.createNewEmployee(new Employee(0, "Bill", "Shields", hrManager.getId(), new Role(0, EmployeeService.ROLE_EMPLOYEE), new ArrayList<Finance>(), null, new Department(0, EmployeeService.DEPARTMENT_HR), 25, 33000, 2000)));
		hrEmployeeList.add(employeeService.createNewEmployee(new Employee(0, "Michael", "Jones", EmployeeService.MANAGER_DEFAULT_ID, new Role(0, EmployeeService.ROLE_EMPLOYEE), new ArrayList<Finance>(), null, new Department(0, EmployeeService.DEPARTMENT_HR), 25, 33000, 2000)));
		
		itEmployeeList.add(employeeService.createNewEmployee(new Employee(0, "Bob", "Robs", itManager.getId(), new Role(0, EmployeeService.ROLE_EMPLOYEE), new ArrayList<Finance>(), null, new Department(0, EmployeeService.DEPARTMENT_IT), 25, 33000, 2000)));
		itEmployeeList.add(employeeService.createNewEmployee(new Employee(0, "June", "Evans", itManager.getId(), new Role(0, EmployeeService.ROLE_EMPLOYEE), new ArrayList<Finance>(), null, new Department(0, EmployeeService.DEPARTMENT_IT), 25, 33000, 2000)));
	}
	
	public void deleteTestData() throws EmployeeDeleteException
	{
		deleteEmployee(hrManager);
		deleteEmployee(itManager);
		
		for (Employee hrWorker : hrEmployeeList)
		{
			deleteEmployee(hrWorker);
		}
		
		for (Employee itWorker : itEmployeeList)
		{
			deleteEmployee(itWorker);
		}
		
		departmentRepository.delete(hrDepartment);
		departmentRepository.delete(itDepartment);
		
		roleRepository.delete(managerRole);
		roleRepository.delete(employeeRole);
	}
	
	private void deleteEmployee(Employee employee) throws EmployeeDeleteException
	{
		if (employeeRepository.findOneById(employee.getId()) != null)
		{	
			employeeService.deleteEmployee(employee);
		}
	}
}
