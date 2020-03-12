package com.killen.services.hr.access.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.killen.services.hr.access.entity.User;
import com.killen.services.hr.access.repository.UserRepository;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler
{
	private static final Logger logger = LogManager.getLogger(CustomAuthenticationFailureHandler.class);
	
	@Autowired
    private UserRepository userRepository;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, 
										HttpServletResponse response,
										AuthenticationException exception) throws IOException, ServletException
	{
		String failedUsername = request.getParameter("username");
		logger.info("Failed login | " + failedUsername);
		
		User failedUser = userRepository.findByUsername(failedUsername);
		
		if (failedUser != null)
		{
			int currentCount = failedUser.getFailedCount();
			
			failedUser.setFailedCount(currentCount + 1);
			failedUser.setUserEnabled(false);
			
			userRepository.save(failedUser);
		}
		
		response.sendRedirect(request.getContextPath() + "/login.html?error");
	}
}
