package com.killen.services.hr.access.component;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodExecutionTimings 
{
	static final Logger logger = LogManager.getLogger(MethodExecutionTimings.class);

	@Around("@annotation(com.killen.services.hr.access.annotation.LogExecutionTime)")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable 
	{
		Object result = null;
		StringBuilder endString = new StringBuilder();
		// Log method start time
		long startTime = System.currentTimeMillis();
		// 	Record the start time
	
		logger.info("Start "+ joinPoint.toShortString() + " | Args " + Arrays.toString(joinPoint.getArgs()));
		
		// Run the method
		try
		{
			result = joinPoint.proceed();
		}
		catch (Exception ex)
		{
			// In cases of error record how long execution took to complete
			long timeTaken = System.currentTimeMillis() - startTime;
			// 	Record the error details
			logger.info("End "+ joinPoint.toShortString() + "() | Time Taken " +  timeTaken + "ms | returns " + ex);
			// Rethrow the error up the stack
			throw ex;
		}
	
		// Record how long execution took to complete
		long timeTaken = System.currentTimeMillis() - startTime;
		// Log the result based on return type
		endString.append("End "+ joinPoint.toShortString() + "() | Time Taken " +  timeTaken + "ms | returns ");
		
		// If the method returns a value
		if (result != null) 
		{
			endString.append(result);	
		}
	
		logger.info(endString);
	
		return result;
	}
}