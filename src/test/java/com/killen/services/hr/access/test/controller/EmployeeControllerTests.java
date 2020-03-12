package com.killen.services.hr.access.test.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.killen.services.hr.access.HrAccess;
import com.killen.services.hr.access.controller.EmployeeController;
import com.killen.services.hr.access.entity.Employee;
import com.killen.services.hr.access.exception.EmployeeCreateException;
import com.killen.services.hr.access.exception.EmployeeDeleteException;
import com.killen.services.hr.access.repository.DepartmentRepository;
import com.killen.services.hr.access.repository.EmployeeRepository;
import com.killen.services.hr.access.repository.RoleRepository;
import com.killen.services.hr.access.service.EmployeeService;
import com.killen.services.hr.access.test.EmployeeTestData;

import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HrAccess.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTests 
{
	@Autowired
    private WebApplicationContext context;
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private EmployeeRepository employeeRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
    private MockMvc mockMvc;
 
    private EmployeeTestData testData;
    
    @PostConstruct
    public void createData() throws EmployeeCreateException    
    {
    	testData = new EmployeeTestData(employeeService, 
										employeeRepository,
										departmentRepository,
										roleRepository);
    }
    @Before
    public void setup() throws EmployeeCreateException 
    {
    	mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }
    
    @After
	public void cleanUp() throws EmployeeDeleteException
	{
		testData.deleteTestData();
	}
	 
    @Test
    @WithMockUser("anonymousUser")
    public void homeToLoginPageWithNoUser() throws Exception
    {
    	MvcResult mvcResult = mockMvc.perform(get("/")).andReturn();
    	
    	assertEquals(EmployeeController.URL_LOGIN, mvcResult.getResponse().getHeader(EmployeeController.HEADER_LOCATION));
    }
    
    @Test
    @WithUserDetails(value="eray", userDetailsServiceBeanName="userDetailsService")
    public void homeToHrPageWithManagerUser() throws Exception
    {
    	MvcResult mvcResult = mockMvc.perform(get("/")).andReturn();
    	
    	assertEquals("/manager", mvcResult.getResponse().getHeader(EmployeeController.HEADER_LOCATION));
    } 
    
    @Test
    @WithUserDetails(value="sturns", userDetailsServiceBeanName="userDetailsService")
    public void homeToHrPageWithHrUser() throws Exception
    {
    	MvcResult mvcResult = mockMvc.perform(get("/")).andReturn();
    	
    	assertEquals("/hr", mvcResult.getResponse().getHeader(EmployeeController.HEADER_LOCATION));
    } 
    
    @Test
    @WithUserDetails(value="jevans", userDetailsServiceBeanName="userDetailsService")
    public void homeToEmployeePageWithEmployeeUser() throws Exception
    {
    	MvcResult mvcResult = mockMvc.perform(get("/")).andReturn();
    	
    	assertEquals(EmployeeController.URL_EMPLOYEE, mvcResult.getResponse().getHeader(EmployeeController.HEADER_LOCATION));
    }
    
    @Test
    @WithMockUser("anonymousUser")
    public void redirectToLoginPageWithNoUser() throws Exception
    {
    	MvcResult mvcResult = mockMvc.perform(get(EmployeeController.URL_REDIRECT)).andReturn();
    	
    	assertEquals(EmployeeController.URL_LOGIN, mvcResult.getResponse().getHeader(EmployeeController.HEADER_LOCATION));
    }
    
    @Test
    @WithUserDetails(value="eray", userDetailsServiceBeanName="userDetailsService")
    public void redirectToHrPageWithManagerUser() throws Exception
    {
    	MvcResult mvcResult = mockMvc.perform(get(EmployeeController.URL_REDIRECT)).andReturn();
    	
    	assertEquals("/manager", mvcResult.getResponse().getHeader(EmployeeController.HEADER_LOCATION));
    } 
    
    @Test
    @WithUserDetails(value="sturns", userDetailsServiceBeanName="userDetailsService")
    public void redirectToHrPageWithHrUser() throws Exception
    {
    	MvcResult mvcResult = mockMvc.perform(get(EmployeeController.URL_REDIRECT)).andReturn();
    	
    	assertEquals("/hr", mvcResult.getResponse().getHeader(EmployeeController.HEADER_LOCATION));
    } 
    
    @Test
    @WithUserDetails(value="jevans", userDetailsServiceBeanName="userDetailsService")
    public void redirectToEmployeePageWithEmployeeUser() throws Exception
    {
    	MvcResult mvcResult = mockMvc.perform(get(EmployeeController.URL_REDIRECT)).andReturn();
    	
    	assertEquals(EmployeeController.URL_EMPLOYEE, mvcResult.getResponse().getHeader(EmployeeController.HEADER_LOCATION));
    }  
        
    @Test
    @WithUserDetails(value="jevans", userDetailsServiceBeanName="userDetailsService")
    public void testEmployeeDataForUser() throws Exception
    {
    	Employee testEmployee = employeeService.getEmployeeByUserName("jevans");
    	
    	mockMvc.perform(get(EmployeeController.URL_EMPLOYEE))
                .andExpect(model().size(3))
                .andExpect(model().attribute(EmployeeController.OBJECT_TITLE, "IT Employee June Evans"))
                .andExpect(model().attribute(EmployeeController.OBJECT_EMPLOYEE, hasProperty("id", is(testEmployee.getId()))))
                .andExpect(model().attribute(EmployeeController.OBJECT_MANAGER_NAME, employeeService.getManagerName(testEmployee)))
                .andExpect(status().isOk());    	
    }
    
    @Test
    @WithUserDetails(value="sturns", userDetailsServiceBeanName="userDetailsService")
    public void testEmployeePageForManager() throws Exception
    {
    	Employee testEmployee = employeeService.getEmployeeByUserName("sturns");
    	
    	mockMvc.perform(get(EmployeeController.URL_EMPLOYEE))
                .andExpect(model().size(3))
                .andExpect(model().attribute(EmployeeController.OBJECT_TITLE, "HR Manager Steve Turns"))
                .andExpect(model().attribute(EmployeeController.OBJECT_EMPLOYEE, hasProperty("id", is(testEmployee.getId()))))
                .andExpect(model().attribute(EmployeeController.OBJECT_MANAGER_NAME, employeeService.getManagerName(testEmployee)))
                .andExpect(status().isOk());
    }
    
    @Test
    @WithUserDetails(value="sturns", userDetailsServiceBeanName="userDetailsService")
    public void testAdminPage() throws Exception
    {
    	mockMvc.perform(get(EmployeeController.URL_ADMIN))
                .andExpect(model().size(3))
                .andExpect(model().attribute(EmployeeController.OBJECT_TITLE, "HR Manager Steve Turns"))
                .andExpect(model().attribute(EmployeeController.OBJECT_ENABLED_EMPLOYEES, hasSize(6)))
                .andExpect(model().attribute(EmployeeController.OBJECT_DISABLED_EMPLOYEES, hasSize(0)))
                .andExpect(status().isOk());
    }
}
