package com.killen.services.hr.access.service;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.killen.services.hr.access.annotation.LogExecutionTime;
import com.killen.services.hr.access.entity.Department;
import com.killen.services.hr.access.entity.Employee;
import com.killen.services.hr.access.entity.Finance;
import com.killen.services.hr.access.entity.Role;
import com.killen.services.hr.access.entity.User;
import com.killen.services.hr.access.exception.DepartmentRetrievalException;
import com.killen.services.hr.access.exception.EmployeeByUserNameException;
import com.killen.services.hr.access.exception.EmployeeCreateException;
import com.killen.services.hr.access.exception.EmployeeDeleteException;
import com.killen.services.hr.access.exception.EmployeeNameException;
import com.killen.services.hr.access.exception.EmployeeRetrievalException;
import com.killen.services.hr.access.exception.EmployeeUpdateException;
import com.killen.services.hr.access.exception.RoleRetrievalException;
import com.killen.services.hr.access.exception.UserInvalidException;
import com.killen.services.hr.access.exception.UserRetrievalException;
import com.killen.services.hr.access.repository.DepartmentRepository;
import com.killen.services.hr.access.repository.EmployeeRepository;
import com.killen.services.hr.access.repository.FinanceRepository;
import com.killen.services.hr.access.repository.RoleRepository;
import com.killen.services.hr.access.repository.UserRepository;


/**
 * 
 */
@Service
public class EmployeeService
{
	private static final Logger logger = LogManager.getLogger(EmployeeService.class);
	
	public static final String DEPARTMENT_HR = "HR";
	public static final String DEPARTMENT_IT = "IT";
	
	public static final String ROLE_MANAGER = "MANAGER";
	public static final String ROLE_EMPLOYEE = "EMPLOYEE";
	
	public static final String FINANCE_SALARY = "SALARY";
	public static final String FINANCE_BONUS = "BONUS";
	
	public static final int MANAGER_DEFAULT_ID = -1;
	
	@Value("${user.default.password}")
	private String defaultPassword;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private FinanceRepository financeRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@LogExecutionTime
	public Employee getEmployeeByUserName(String username) throws EmployeeByUserNameException
	{
		User user = userRepository.findByUsername(username);
		
		if (user != null)
		{
			Employee employee = employeeRepository.findOneByUserId(user.getId());
			
			if (employee != null)
			{
				return employee;
			}
		}
		else
		{
			logger.error("Error finding Employee by username | " + username); 
			throw new EmployeeByUserNameException(username);
		}
		
		return null;
	}
	
	@LogExecutionTime
	public Employee createNewEmployee(Employee newEmployee) throws EmployeeCreateException
	{
		Employee tempEmployee = null;
		
		try
		{
			logger.info("Creating New Employee  | " + newEmployee.toString());
			
			// Ensure valid names are set for an employee object
			checkEmployeeNamesValid(newEmployee);
			
			// Assign roles
			newEmployee.setRole(getRole(newEmployee.getRole().getId(),
										newEmployee.getRole().getType()));
		
			// Place in correct department
			newEmployee.setDepartment(getDepartment(newEmployee.getDepartment().getId(),
													newEmployee.getDepartment().getName()));
		
			// Create the new user account 
			newEmployee.setUser(createEmployeeUser(newEmployee.getFirstName(), newEmployee.getLastName()));
		
			// Save employee information
			tempEmployee = employeeRepository.save(newEmployee);
		}
		catch (Exception ex)
		{
			logger.error("Error saving new employee " + newEmployee + " | " + ex);
			
			throw new EmployeeCreateException(newEmployee);
		}
		
		return tempEmployee;	
	}
	
	@LogExecutionTime
	public boolean deleteEmployee(int employeeId) throws EmployeeDeleteException
	{
		return deleteEmployee(getEmployeeById(employeeId));
	}
	
	@LogExecutionTime
	public boolean deleteEmployee(Employee employee) throws EmployeeDeleteException
	{
		try
		{
			logger.info("Deleting Employee  | " + employee.toString());
			
			// If an employee is a manager reset any existing team members
			if (employee.isManager())
			{
				List<Employee> teamMembers = getAllEmployeesByManagerId(employee.getId());
				
				for (Employee tempEmployee : teamMembers)
				{
					tempEmployee.setManagerId(MANAGER_DEFAULT_ID);
					employeeRepository.save(tempEmployee);
				}
			}
			
			employeeRepository.delete(getEmployee(employee.getId()));
			
			userRepository.delete(getUser(employee.getUser().getId()));
		}
		catch (Exception ex)
		{
			logger.error("Error deleting new employee " + employee.toString() + " | " + ex);
			
			throw new EmployeeDeleteException(employee);
		}
				
		return true;		
	}
	
	@LogExecutionTime
	public Employee updateEmployee(Employee updatedEmployee) throws EmployeeUpdateException
	{
		logger.info("Updating Employee");
		
		Employee savedEmployee = null;
		
		try
		{
			logger.info("Employee Information | " + updatedEmployee.toString());
			Employee employee = employeeRepository.findOneById(updatedEmployee.getId());
			// Ensure that a valid employee is updated 
			if (employee != null)
			{
				boolean nameChanged = false;
				
				checkEmployeeNamesValid(updatedEmployee);
				
				if (!employee.getFirstName().equals(updatedEmployee.getFirstName()) ||
					!employee.getLastName().equals(updatedEmployee.getLastName()))
				{
					logger.info("Employee name change, user update required");
					nameChanged = true;
				}
				
				if (nameChanged)
				{
					String newUsername = checkUsername(updatedEmployee.getFirstName(), updatedEmployee.getLastName());
					User updatedUser = employee.getUser();
					
					logger.info("Employee username old | " + employee.getUser().getUsername() + " new | " + newUsername);
					updatedUser.setUsername(newUsername);
					userRepository.save(updatedUser);
					updatedEmployee.setUser(updatedUser);
				}
				else
				{
					updatedEmployee.setUser(employee.getUser());
				}
				
				if (employee.getSalary() != updatedEmployee.getSalary() ||
					employee.getBonus() != updatedEmployee.getBonus())
				{
					List<Finance> financeList = employee.getFinances();
					
					if (employee.getSalary() != updatedEmployee.getSalary())
					{
						logger.info("Adding SALARY info to history for " + employee.getFullName()); 
						Finance financeSalaryRecord = new Finance(0, new Date(), FINANCE_SALARY, employee.getSalary(), updatedEmployee.getSalary());
						financeRepository.save(financeSalaryRecord);
						financeList.add(financeSalaryRecord);	
					}
					
					if (employee.getBonus() != updatedEmployee.getBonus())
					{
						logger.info("Adding BONUS info to history for " + employee.getFullName()); 
						Finance financeBonusRecord = new Finance(0, new Date(), FINANCE_BONUS, employee.getBonus(), updatedEmployee.getBonus());
						financeRepository.save(financeBonusRecord);
						financeList.add(financeBonusRecord);
					}
					
					updatedEmployee.setFinances(financeList);
				}
							
				// Save employee information
				savedEmployee = employeeRepository.save(updatedEmployee);
			}
			else
			{
				logger.error("Updated employee does not exist | " + updatedEmployee);
				
			}
		}
		catch (Exception ex)
		{
			logger.error("Error when updating employee | " + updatedEmployee + " " + ex);
			throw new EmployeeUpdateException(updatedEmployee);
		}
				
		return savedEmployee;
	}
	
	@LogExecutionTime
	public List<Employee> getAllEmployeesByManagerId(int managerId)
	{
		return employeeRepository.findAllByManagerId(managerId, defaultEmployeeSort());
	}
	
	@LogExecutionTime
	public List<Employee> getAllManagers()
	{
		return employeeRepository.findAllByRoleId(roleRepository.findByType(ROLE_MANAGER).getId(), defaultEmployeeSort());
	}
	
	@LogExecutionTime
	public List<Employee> getAllEmployeesNotInHrDepartment()
	{
		return employeeRepository.findByDepartmentNameNot(DEPARTMENT_HR, defaultEmployeeSort());
	}
	
	@LogExecutionTime
	public List<Employee> getAllNonHrEmployees(int managerId)
	{
		return employeeRepository.findByManagerIdNotAndDepartmentNameNot(managerId, DEPARTMENT_HR, defaultEmployeeSort());
	}
	
	@LogExecutionTime
	public Employee getEmployeeById(int employeeId)
	{
		return employeeRepository.findOneById(employeeId);
	}
	
	@LogExecutionTime
	public String getManagerName(Employee employee)
	{
		Employee manager = null;
		String managerName = "";
		
		if (employee.getManagerId() == MANAGER_DEFAULT_ID)
		{
			logger.info("No manager set for employee | " + employee.getFullName());
			return "N/A";
		}
		else
		{
			manager = getEmployeeById(employee.getManagerId());
			
			if (manager != null && 
				manager.getRole().getType().equals(ROLE_MANAGER))
			{
				managerName = manager.getFullName();
			}
		}
		
		return managerName;
	}
	
	@LogExecutionTime
	public List<Role> getAllEmployeeRoles()
	{
		return roleRepository.findAll();
	}
	
	@LogExecutionTime
	public List<Department> getAllEmployeeDepartments()
	{
		return departmentRepository.findAll();
	}
	
	@LogExecutionTime
	public List<Employee> getEnabledEmployees()
	{
		return employeeRepository.findByUserUserEnabled(true, defaultEmployeeSort());
	}
	
	@LogExecutionTime
	public List<Employee> getDisabledEmployees()
	{
		return employeeRepository.findByUserUserEnabled(false, defaultEmployeeSort());
	}
	
	@LogExecutionTime
	public boolean disableEmployeeUserAcount(int userId) throws UserInvalidException
	{
		return updateEmployeeUserAccountEnabled(userId, false);
	}
	
	@LogExecutionTime
	public boolean enableEmployeeUserAcount(int userId) throws UserInvalidException
	{
		return updateEmployeeUserAccountEnabled(userId, true);
	}
	
	@LogExecutionTime
	public boolean updateEmployeeUserAccountEnabled(int userId, boolean isEnabled) throws UserInvalidException
	{
		User user = userRepository.findOneById(userId);
		
		if (user != null)
		{
			user.setUserEnabled(isEnabled);
			userRepository.save(user);
			return true;
		}
		else
		{
			logger.error("Unable to find user | " + userId);
			throw new UserInvalidException(userId);
		}
	}
	
	private User createEmployeeUser(String firstName, String lastName)
	{
		return userRepository.save(new User(checkUsername(firstName, lastName), 
											bCryptPasswordEncoder.encode(defaultPassword)));
	}
	
	private String checkUsername(String firstName, String lastName)
	{
		String tempUsername = generateUsername(firstName, lastName);
		boolean usernameExists = false;
		int count = 0;
		
		do
		{
			usernameExists = userRepository.existsByUsername(tempUsername);	
			
			if (usernameExists)
			{
				count ++;
				tempUsername = generateUsername(firstName, lastName) + count;				
			}
		} 
		while (usernameExists);
		
		return tempUsername;
	}
	
	private String generateUsername(String firstName, String lastName)
	{
		String tempUsername = firstName.charAt(0) + lastName;
		
		return tempUsername.toLowerCase();
	}
	
	private Employee getEmployee(int employeeId) throws EmployeeRetrievalException
	{
		Employee employee = null;
		
		try
		{
			employee = employeeRepository.findOneById(employeeId);
		}
		catch(Exception ex)
		{
			logger.error("Error getting employee " + employeeId +" | " + ex);
		}
		
		if (employee == null)
		{
			throw new EmployeeRetrievalException(employeeId);
		}
		
		return employee;
	}
	
	private Role getRole(int roleId, String roleType) throws RoleRetrievalException
	{
		Role tempRole = null;
		
		try
		{
			if (roleType != null && roleType.length() > 0)
			{
				tempRole = roleRepository.findByType(roleType);
			}
			else
			{
				tempRole = roleRepository.findOneById(roleId);
			}					
		}
		catch(Exception ex)
		{
			logger.error("Error getting role | " + ex);
		}
		
		if (tempRole == null)
		{
			if (roleType != null && roleType.length() > 0)
			{
				throw new RoleRetrievalException(roleType);
			}
			else
			{
				throw new RoleRetrievalException(roleId);
			}
		}
		
		return tempRole;
	}
	
	private Department getDepartment(int departmentId, String departmentName) throws DepartmentRetrievalException
	{
		Department department = null;
		
		try
		{
			if (departmentName != null && departmentName.length() > 0)
			{
				department = departmentRepository.findByName(departmentName);
			}
			else
			{
				department = departmentRepository.findOneById(departmentId);
			}
		}
		catch (Exception ex)
		{
			logger.error("Error getting department | " + ex);
		}
		
		if (department == null)
		{
			if (departmentName != null && departmentName.length() > 0)
			{
				throw new DepartmentRetrievalException(departmentName);
			}
			else
			{
				throw new DepartmentRetrievalException(departmentId);
			}
		}
		
		return department;
	}
	
	private User getUser(int userId) throws UserRetrievalException
	{
		User user = null;
		
		try
		{
			user = userRepository.findOneById(userId);
		}
		catch (Exception ex)
		{
			logger.error("Error getting user " + userId +" | " + ex);
		}
		
		if (user == null)
		{
			throw new UserRetrievalException(userId);
		}
		
		return user;
	}
	
	private boolean checkEmployeeNamesValid(Employee employee) throws EmployeeNameException
	{
		if (employee.getFirstName() == null || employee.getFirstName().length() == 0 ||
			employee.getLastName() == null || employee.getLastName().length() == 0)
		{
			throw new EmployeeNameException(employee.getFirstName(), employee.getLastName());
		}
		
		return true;
	}
	
	private Sort defaultEmployeeSort()
	{
		Sort sort = Sort.by(Sort.Order.asc("firstName"),
							Sort.Order.asc("lastName"));
		
		return sort;
	}
}
