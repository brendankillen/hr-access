package com.killen.services.hr.access.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.killen.services.hr.access.entity.User;
import com.killen.services.hr.access.repository.IUserRepository;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
	private static final Logger logger = LogManager.getLogger(CustomAuthenticationFailureHandler.class);

	private IUserRepository userRepository;

	public CustomAuthenticationFailureHandler(IUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String failedUsername = request.getParameter("username");
		logger.info("Failed login by user | FailedUsername={} |", failedUsername);

		User failedUser = userRepository.findByUsername(failedUsername);

		if (failedUser != null) {
			int currentCount = failedUser.getFailedCount();

			failedUser.setFailedCount(currentCount + 1);
			failedUser.setUserEnabled(false);

			userRepository.save(failedUser);
		}

		response.sendRedirect(request.getContextPath() + "/login.html?error");
	}
}
