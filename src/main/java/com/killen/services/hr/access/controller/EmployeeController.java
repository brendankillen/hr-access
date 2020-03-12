package com.killen.services.hr.access.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.killen.services.hr.access.annotation.LogExecutionTime;
import com.killen.services.hr.access.component.IAuthenticationFacade;
import com.killen.services.hr.access.entity.Department;
import com.killen.services.hr.access.entity.Employee;
import com.killen.services.hr.access.entity.Role;
import com.killen.services.hr.access.exception.EmployeeByUserNameException;
import com.killen.services.hr.access.exception.EmployeeCreateException;
import com.killen.services.hr.access.exception.EmployeeDeleteException;
import com.killen.services.hr.access.exception.EmployeeInvalidAccessError;
import com.killen.services.hr.access.exception.EmployeeUpdateException;
import com.killen.services.hr.access.exception.UnauthorizedAccessException;
import com.killen.services.hr.access.exception.UserInvalidException;
import com.killen.services.hr.access.service.EmployeeService;

@Controller
public class EmployeeController 
{
	private static final Logger logger = LogManager.getLogger(EmployeeController.class);
	
	public static final String ANONUMOUS_USER = "anonymousUser";
	
	public static final String URL_REDIRECT = "/redirect";
	public static final String URL_LOGIN = "/login";
	public static final String URL_EMPLOYEE = "/employee";
	public static final String URL_MANAGER = "/manager";
	public static final String URL_HR = "/hr";
	public static final String URL_ADMIN = "/admin";
	
	public static final String HEADER_LOCATION = "Location";
	
	public static final String OBJECT_TITLE = "title";
	public static final String OBJECT_EMPLOYEE = "employee";
	public static final String OBJECT_EMPLOYEES = "employees";
	public static final String OBJECT_ENABLED_EMPLOYEES = "enabledEmployees";
	public static final String OBJECT_DISABLED_EMPLOYEES = "disabledEmployees";
	public static final String OBJECT_MANAGER = "manager";
	public static final String OBJECT_MANAGERS = "managers";
	public static final String OBJECT_MANAGER_NAME = "managerName";
	public static final String OBJECT_ROLES = "roles";
	public static final String OBJECT_DEPARTMENTS = "departments";
	public static final String OBJECT_TEAM = "team";
	public static final String OBJECT_NON_HR_EMPLOYEES = "allNonHrEmployees";
	
	public static final String VIEW_EMPLOYEE = "employee";
	public static final String VIEW_EMPLOYEE_NEW = "employee-new";
	public static final String VIEW_EMPLOYEE_EDIT = "employee-edit";
	public static final String VIEW_MANAGER = "manager";
	public static final String VIEW_HR = "hr";
	public static final String VIEW_ADMIN = "admin";
	
	@Autowired
    private IAuthenticationFacade authenticationFacade;
	
	@Autowired
	EmployeeService employeeService;
	
	@LogExecutionTime
	@RequestMapping(value = {"/", URL_REDIRECT}, method = RequestMethod.GET)
	public void redirect(HttpServletResponse response) throws EmployeeByUserNameException, 
															  UnauthorizedAccessException
	{
		String user = currentUserName();
		
		if (user.equals(ANONUMOUS_USER))
		{
			logger.info("Redirecting to Login page");
			response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
			response.setHeader(HEADER_LOCATION, URL_LOGIN);
			
			return;
		}
		
		Employee employee = employeeService.getEmployeeByUserName(user);
		
		if (employee.isHr())
		{
			logger.info("Redirecting to HR page");
			response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
			response.setHeader(HEADER_LOCATION, "/hr");
			
			return;
		}
		else if (employee.isManager())
		{
			logger.info("Redirecting to MANAGER page");
			response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
			response.setHeader(HEADER_LOCATION, "/manager");
			
			return;
		}
		
		logger.info("Redirecting to EMPLOYEE page");
		response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
		response.setHeader(HEADER_LOCATION, URL_EMPLOYEE);
	}
	
	@LogExecutionTime
	@GetMapping(URL_EMPLOYEE)
	public ModelAndView showEmployee() throws EmployeeByUserNameException,
											  UnauthorizedAccessException
	{
		ModelAndView modelAndView = new ModelAndView();
		
		Employee employee = employeeService.getEmployeeByUserName(currentUserName());
		
		logger.info("Show  Employee | " + employee.toString());
		
		String managerName = employeeService.getManagerName(employee);
		String title = buildTitle(employee);
		
		modelAndView.addObject(OBJECT_TITLE, title); 
		modelAndView.addObject(OBJECT_EMPLOYEE, employee); 
		modelAndView.addObject(OBJECT_MANAGER_NAME, managerName); 
		modelAndView.setViewName(VIEW_EMPLOYEE);
		
		return modelAndView;
	}
	
	@LogExecutionTime
	@GetMapping(URL_EMPLOYEE + "/new")
	public ModelAndView showNewEmployee()
	{
		logger.info("New Employee");
		ModelAndView modelAndView = new ModelAndView();
		
		Employee employee = new Employee();
		List<Employee> managers = employeeService.getAllManagers();
		List<Role> roles = employeeService.getAllEmployeeRoles();
		List<Department> departments = employeeService.getAllEmployeeDepartments();
		String title = "New Employee";

		modelAndView.addObject(OBJECT_TITLE, title); 
		modelAndView.addObject(OBJECT_EMPLOYEE, employee); 
		modelAndView.addObject(OBJECT_MANAGERS, managers);
		modelAndView.addObject(OBJECT_ROLES, roles);
		modelAndView.addObject(OBJECT_DEPARTMENTS, departments);
		modelAndView.setViewName(VIEW_EMPLOYEE_NEW);
		
		return modelAndView;
	}
	
	@LogExecutionTime
	@PostMapping(URL_EMPLOYEE + "/new")
	public RedirectView addNewEmployee(@ModelAttribute("employee") Employee newEmployee) throws EmployeeCreateException
	{
		logger.info("Adding new Employee | " + newEmployee.toString());
		
		employeeService.createNewEmployee(newEmployee);
		
		return new RedirectView(URL_REDIRECT);
	}
	
	@LogExecutionTime
	@GetMapping(URL_EMPLOYEE + "/{id}")
	public ModelAndView showEditEmployee(@PathVariable("id") int employeeId,
										 @RequestParam("view") boolean isView) throws EmployeeByUserNameException,
																					  EmployeeInvalidAccessError,
																					  UnauthorizedAccessException
	{
		ModelAndView modelAndView = new ModelAndView();
		// Get the logged in user
		Employee loggedInEmployee = employeeService.getEmployeeByUserName(currentUserName());
		// Get the employee that you are looking up details on
		Employee employeeLookup = employeeService.getEmployeeById(employeeId);
		
		// Ensure that if a lookup is happening that a HR employee is not looking up an employee
		// using direct url access
		if (employeeLookup.getDepartment().getName().equals(EmployeeService.DEPARTMENT_HR))
		{
			if (employeeLookup.getManagerId() != loggedInEmployee.getId())
			{
				logger.error("Invalid access to employee data | " + employeeLookup.getFullName() + " | by employee | " + loggedInEmployee.getFullName());
				throw new EmployeeInvalidAccessError(employeeLookup.getFullName(), loggedInEmployee.getFullName());
			}
		}
		
		logger.info("Employee | " + employeeLookup.toString());
		
		List<Employee> managers = employeeService.getAllManagers();
		List<Role> roles = employeeService.getAllEmployeeRoles();
		List<Department> departments = employeeService.getAllEmployeeDepartments();
		String title = buildTitle(employeeLookup);
		String managerName = employeeService.getManagerName(employeeLookup);

		modelAndView.addObject(OBJECT_TITLE, title); 
		modelAndView.addObject(OBJECT_EMPLOYEE, employeeLookup); 
		modelAndView.addObject(OBJECT_MANAGERS, managers);
		modelAndView.addObject(OBJECT_ROLES, roles);
		modelAndView.addObject(OBJECT_DEPARTMENTS, departments);
		modelAndView.addObject(OBJECT_MANAGER_NAME, managerName);
		
		if (isView == true)
		{
			logger.info("Viewing  Employee | " + employeeLookup.toString());
			modelAndView.setViewName(OBJECT_EMPLOYEE);
		}
		else
		{
			logger.info("Editing  Employee | " + employeeLookup.toString());
			modelAndView.setViewName(VIEW_EMPLOYEE_EDIT);
		}
		
		return modelAndView;
	}
	
	@LogExecutionTime
	@PostMapping(URL_EMPLOYEE + "/{id}")
	public RedirectView updateEmployee(@PathVariable("id") String id,
            						   @ModelAttribute("employee") Employee employee) throws EmployeeUpdateException 
	{
		logger.info("Updated Employee | " + employee.toString());
		
		employeeService.updateEmployee(employee);
		
		return new RedirectView(URL_REDIRECT);
	}
	
	@LogExecutionTime
	@DeleteMapping(URL_EMPLOYEE + "/{id}")
	public RedirectView deleteEmployee(@PathVariable("id") int employeeId) throws EmployeeDeleteException 
	{
		logger.info("Deleting Employee | " + employeeId);
		
		employeeService.deleteEmployee(employeeId);
		
		return new RedirectView(URL_REDIRECT);
	}
	
	@LogExecutionTime
	@GetMapping(URL_MANAGER)
	public ModelAndView showEmployeeManager() throws EmployeeByUserNameException,
													 UnauthorizedAccessException
	{
		ModelAndView modelAndView = new ModelAndView();
		
		Employee manager = employeeService.getEmployeeByUserName(currentUserName());
		
		logger.info("Showing Manager screen | " + manager.toString());
		List<Employee> employees = employeeService.getAllEmployeesByManagerId(manager.getId());
		String title = buildTitle(manager);
		
		modelAndView.addObject(OBJECT_TITLE, title);
		modelAndView.addObject(OBJECT_MANAGER, manager); 
		modelAndView.addObject(OBJECT_EMPLOYEES, employees); 
		modelAndView.setViewName(VIEW_MANAGER);
		
		return modelAndView;
	}
	
	@LogExecutionTime
	@GetMapping(URL_HR)
	public ModelAndView showEmployeeHr() throws EmployeeByUserNameException,
												UnauthorizedAccessException
	{
		ModelAndView modelAndView = new ModelAndView();
		List<Employee> team = new ArrayList<>();
		List<Employee> allNonHrEmployees = new ArrayList<>();
		String title = "";
		
		// Get the current logged in employee 
		Employee employee = employeeService.getEmployeeByUserName(currentUserName());
		
		logger.info("Showing HR screen | " + employee.toString());
		
		title = buildTitle(employee);
		
		// If the employee is a manager get all there team members
		if (employee.isManager())
		{
			team = employeeService.getAllEmployeesByManagerId(employee.getId());
			// Get all the rest of the employees excluding the team employees
			allNonHrEmployees = employeeService.getAllNonHrEmployees(employee.getId());
		}
		else
		{
			// Get all the the employees excluding other HR employees
			allNonHrEmployees = employeeService.getAllEmployeesNotInHrDepartment();
		}
		
		modelAndView.addObject(OBJECT_TITLE, title);
		modelAndView.addObject(OBJECT_EMPLOYEE, employee); 
		modelAndView.addObject(OBJECT_TEAM, team); 
		modelAndView.addObject(OBJECT_NON_HR_EMPLOYEES, allNonHrEmployees); 
		modelAndView.setViewName(VIEW_HR);
		
		return modelAndView;
	}
	
	@LogExecutionTime
	@GetMapping(URL_ADMIN)
	public ModelAndView getEmpoyeeUserInformation() throws EmployeeByUserNameException, 
														   UnauthorizedAccessException
	{
		ModelAndView modelAndView = new ModelAndView();
		
		Employee employee = employeeService.getEmployeeByUserName(currentUserName());
		
		logger.info("Showing Admin screen | " + employee.toString());
		
		List<Employee> enabledEmployees = employeeService.getEnabledEmployees();
		// Remove the current user for this list as we don't want them to lock themselves out
		enabledEmployees.removeIf(e -> e.getId() == employee.getId());
		List<Employee> disabledEmployees = employeeService.getDisabledEmployees();		
		String title = buildTitle(employee);
		
		modelAndView.addObject(OBJECT_TITLE, title);
		modelAndView.addObject(OBJECT_ENABLED_EMPLOYEES, enabledEmployees); 
		modelAndView.addObject(OBJECT_DISABLED_EMPLOYEES, disabledEmployees);  
		modelAndView.setViewName(VIEW_ADMIN);
		
		return modelAndView;
	}
	
	@LogExecutionTime
	@PostMapping(URL_ADMIN + "/{userId}/{action}")
	public RedirectView getEmpoyeeUserAction(@PathVariable("userId") int userId,
											 @PathVariable("action") boolean action) throws UserInvalidException
	{
		if (action == true)
		{
			employeeService.enableEmployeeUserAcount(userId);
		}
		else if (action == false)
		{
			employeeService.disableEmployeeUserAcount(userId);
		}
		
		return new RedirectView("/" + VIEW_ADMIN);
	}
	
	private String currentUserName() throws UnauthorizedAccessException 
	{
        Authentication authentication = authenticationFacade.getAuthentication();
        
        if (authentication.getName().isBlank())
        {
        	logger.error("Unauthorized access, no valid user");
        	throw new UnauthorizedAccessException();
        }
      
        return authentication.getName();
    }
	
	private String buildTitle(Employee employee)
	{
		if (employee != null)
		{
			return employee.getDepartment().getName() + " " +
				   StringUtils.capitalize(employee.getRole().getType().toLowerCase()) + " " + 
				   employee.getFullName();
		}
		
		return "Invalid Employee";
	}
}
