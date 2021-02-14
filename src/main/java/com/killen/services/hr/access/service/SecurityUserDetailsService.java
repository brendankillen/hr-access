package com.killen.services.hr.access.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.killen.services.hr.access.entity.Employee;
import com.killen.services.hr.access.entity.User;
import com.killen.services.hr.access.model.SecurityUser;
import com.killen.services.hr.access.repository.IEmployeeRepository;
import com.killen.services.hr.access.repository.IUserRepository;

@Service
public class SecurityUserDetailsService implements UserDetailsService
{
	private static final Logger logger = LogManager.getLogger(SecurityUserDetailsService.class);
	
	private IEmployeeRepository employeeRepository; 
	private IUserRepository userRepository;
	
	public SecurityUserDetailsService(IUserRepository userRepository, IEmployeeRepository employeeRepository)
	{
		this.employeeRepository = employeeRepository;
		this.userRepository = userRepository; 
	}
	
    @Override
    public UserDetails loadUserByUsername(String username) 
    {
    	logger.info("Looking up user by | UserName={}", username);
    	
    	List<String> authorities = new ArrayList<>();
    	
    	User user = userRepository.findByUsername(username);
    	Employee employee = employeeRepository.findOneByUserId(user.getId());
        // Roles 
    	if (user != null)
    	{
    		authorities.add(user.getPrivilege());
    	}
    	
        if (employee != null)
        {
        	authorities.add(employee.getRole().getType());
        	authorities.add(employee.getDepartment().getName());        	
        }
        
        if (user.getFailedCount() > 0)
    	{
    		user.setFailedCount(0);   		
    		user = userRepository.save(user);
    	}
        
        return new SecurityUser(user, authorities);
    }
}